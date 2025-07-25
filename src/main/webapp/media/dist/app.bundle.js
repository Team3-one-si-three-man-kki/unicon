(function () {
  'use strict';

  // client/utils/EventEmitter.js

  class EventEmitter {
    constructor() {
      this.events = {};
    }

    // 이벤트 구독
    on(eventName, listener) {
      if (!this.events[eventName]) {
        this.events[eventName] = [];
      }
      this.events[eventName].push(listener);
    }

    // 이벤트 방송
    emit(eventName, ...args) {
      if (this.events[eventName]) {
        this.events[eventName].forEach((listener) => listener(...args));
      }
    }

    // 모든 리스너 제거
    removeAllListeners() {
      this.events = {};
    }
  }

  class RoomClient extends EventEmitter {
    constructor(uiManager) {
      super();
      this.uiManager = uiManager;

      this.ws = null;
      this.device = null;
      this.sendTransport = null;
      this.recvTransport = null;
      this.localStream = null;
      this.producers = new Map();
      this.consumers = new Map();
      this.producerIdToConsumer = new Map(); //   producerId -> consumer 맵
      this.producerToPeerIdMap = new Map(); // producerId -> peerId 맵 추가
      this.actionCallbackMap = new Map();
      this.pendingConsumeList = [];
      this.isAdmin = false; //    관리자 여부
      this.screenProducer = null; //    화면 공유 프로듀서
      this.myPeerId = null; // ✅ 자신의 peerId를 저장할 속성 추가
      this.userName = null;
    }

    join(roomId, userName, userEmail, tenantId) {
      this.userName = userName;
      //    roomId를 인자로 받습니다.
      if (!roomId) {
        throw new Error("roomId is required to join a room");
      }
      //    WebSocket 접속 주소에 roomId를 쿼리 파라미터로 추가합니다.
      // WebSocket 접속 주소를 현재 페이지의 호스트 주소(IP 또는 도메인)를 동적으로 사용하도록 수정합니다.
      // 이렇게 하면 서버 주소가 변경되어도 클라이언트 코드를 수정할 필요가 없습니다.
      // 포트는 3000으로 고정합니다.
      const wsUrl = `wss://${"13.125.229.206:3000"}/?roomId=${roomId}&userName=${encodeURIComponent(userName)}&userEmail=${encodeURIComponent(userEmail)}&tenantId=${encodeURIComponent(tenantId)}`;
      console.log(`Connecting to WebSocket: ${wsUrl}`);
      this.ws = new WebSocket(wsUrl);
      this.ws.onopen = () => {
        console.log("   WebSocket connected");
        this.emit("connected", this.ws); // main.js에 연결 성공을 알림

        // CanvasModule 초기화 로직을 main.js로 이동시켰으므로 이 코드는 제거합니다.

        try {
          this.device = new window.mediasoupClient.Device();
          this.ws.send(JSON.stringify({ action: "getRtpCapabilities" }));
        } catch (err) {
          console.error("    Device creation failed:", err);
        }
      };

      this.ws.onmessage = async (event) => {
        const msg = JSON.parse(event.data);
        console.log("    Received:", msg);

        const cb = this.actionCallbackMap.get(msg.action);
        if (cb) {
          cb(msg);
          this.actionCallbackMap.delete(msg.action);
          return;
        }

        switch (msg.action) {
          case "adminInfo":
            this.isAdmin = msg.data.isAdmin;
            this.myPeerId = msg.data.peerId; // 이 시점에서 myPeerId가 설정됨
            console.log("RoomClient received adminInfo:", msg.data);
            this.emit("adminStatus", msg.data); // UI 매니저에게 알림
            break;
          case "canvas": // 추가된 부분
            this.emit("canvas", msg.data); // 추가된 부분
            break; // 추가된 부분
          case "rtpCapabilities":
            await this._handleRtpCapabilities(msg.data);
            break;
          case "createTransportResponse":
            await this._handleCreateTransportResponse(msg.data);
            break;
          case "createConsumerTransportResponse":
            await this._handleCreateConsumerTransportResponse(msg.data);
            break;
          case "existingProducers":
            await this._handleExistingProducers(msg.data);
            break;
          case "newProducerAvailable":
            await this._handleNewProducerAvailable(msg);
            break;
          // case "consumeResponse":
          //   await this._handleConsumeResponse(msg.data);
          //   break;
          case "producerClosed":
            this._handleProducerClosed(msg);
            break;
          // ✅ [추가] 다른 참여자의 프로듀서 상태 변경 알림을 처리
          case "producerStateChanged": {
            const { producerId, kind, state, userName } = msg.data;
            if (state === "pause") {
              if (kind === "video")
                this.emit("remote-producer-pause", { producerId, userName });
              // 필요하다면 오디오 pause 처리도 추가
              if (kind === "audio")
                this.emit("remote-audio-pause", { producerId });
            } else if (state === "resume") {
              if (kind === "video")
                this.emit("remote-producer-resume", { producerId });
              // 필요하다면 오디오 resume 처리도 추가
              if (kind === "audio")
                this.emit("remote-audio-resume", { producerId });
            }
            break;
          }
          // dominantSpeaker 이벤트 처리
          case "dominantSpeaker": {
            const { producerId, peerId } = msg.data;
            this.emit("dominantSpeaker", { producerId, peerId });
            break;
          }
        }
      };
    }

    sendPeerStatus(statusData) {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(
          JSON.stringify({
            action: "updatePeerStatus",
            data: statusData,
          })
        );
      }
    }

    _waitForAction(actionName, callback) {
      this.actionCallbackMap.set(actionName, callback);
    }

    async _handleRtpCapabilities(data) {
      try {
        await this.device.load({ routerRtpCapabilities: data });
        console.log("   Device loaded successfully");
        this.ws.send(JSON.stringify({ action: "createTransport" }));
      } catch (err) {
        console.error("    Failed to load device capabilities:", err);
      }
    }

    async _handleCreateTransportResponse(data) {
      this.sendTransport = this.device.createSendTransport(data);

      this.sendTransport.on("connect", ({ dtlsParameters }, callback, errback) => {
        this.ws.send(JSON.stringify({ action: "connectTransport", data: { dtlsParameters } }));
        this._waitForAction("transportConnected", callback);
      });

      this.sendTransport.on("produce", async ({ kind, rtpParameters, appData }, callback, errback) => {
        try {
          const { id } = await this._sendRequest("produce", { kind, rtpParameters, appData });
          this.emit('producer-created', { kind, producerId: id });
          callback({ id });
        } catch (error) {
          errback(error);
        }
      });

      if (!this.myPeerId) {
        await new Promise(resolve => this.once("adminStatus", resolve));
      }
      await this._startProducing();
    }

    async _startProducing() {
      try {
        this.localStream = await navigator.mediaDevices.getUserMedia({
          video: { width: { ideal: 640 }, height: { ideal: 480 } },
          audio: true,
        });

        const videoElement = document.createElement("video");
        videoElement.id = "localVideo";
        videoElement.muted = true;
        videoElement.autoplay = true;
        videoElement.playsInline = true;
        videoElement.style.cssText = "height: 100%; width: 100%; object-fit: cover;";
        videoElement.srcObject = this.localStream;

        // 2. 생성된 video 요소와 peerId를 UI 로직으로 전달
        this.emit("localStreamReady", videoElement, this.myPeerId);

        const videoTrack = this.localStream.getVideoTracks()[0];
        const audioTrack = this.localStream.getAudioTracks()[0];

        // 3. produce를 호출하고, 반환된 실제 Producer 객체를 맵에 저장
        if (videoTrack) {
          const videoProducer = await this.sendTransport.produce({ track: videoTrack });
          this.producers.set(videoProducer.id, videoProducer);
        }
        if (audioTrack) {
          const audioProducer = await this.sendTransport.produce({ track: audioTrack });
          this.producers.set(audioProducer.id, audioProducer);
        }

        this.ws.send(JSON.stringify({ action: "deviceReady" }));
        this.emit("controlsReady");

      } catch (err) {
        console.error("CRITICAL: Failed to get user media.", err);
        alert(`카메라/마이크를 가져올 수 없습니다: ${err.name}`);
      }
    }

    async _handleCreateConsumerTransportResponse(data) {
      this.recvTransport = this.device.createRecvTransport(data);
      this.recvTransport.on("connect", ({ dtlsParameters }, callback) => {
        this.ws.send(
          JSON.stringify({
            action: "connectConsumerTransport",
            data: { dtlsParameters },
          })
        );
        this._waitForAction("consumerTransportConnected", callback);
      });

      //    recvTransport가 준비되었으므로, 대기 중인 모든 consumer를 처리합니다.
      const pendingConsumes = [...this.pendingConsumeList];
      this.pendingConsumeList = [];
      console.log(
        `   RecvTransport ready. Processing ${pendingConsumes.length} pending consumers.`
      );
      for (const consumeData of pendingConsumes) {
        await this._consume(consumeData);
      }
    }

    async _handleExistingProducers(producers) {
      console.log(`📋 Found ${producers.length} existing producers.`);
      for (const producer of producers) {
        this.pendingConsumeList.push(producer);
      }

      //    recvTransport가 아직 없으면 생성을 요청하고,
      //    이미 있다면 바로 대기열을 처리하여 타이밍 문제를 해결합니다.
      if (!this.recvTransport) {
        this.ws.send(JSON.stringify({ action: "createConsumerTransport" }));
      } else {
        const pendingConsumes = [...this.pendingConsumeList];
        this.pendingConsumeList = [];
        for (const consumeData of pendingConsumes) {
          await this._consume(consumeData);
        }
      }
    }

    async _handleNewProducerAvailable(producerInfo) {
      console.log("     A new producer is available.", producerInfo);
      const { producerId, kind, appData } = producerInfo;
      const consumeData = { producerId, kind, appData }; // appData도 전달

      //    recvTransport가 없으면 대기열에 추가
      if (!this.recvTransport) {
        this.pendingConsumeList.push(consumeData);
      } else {
        await this._consume(consumeData);
      }
    }

    async _consume({ producerId, kind, appData }) {
      //    중복 consumer 생성을 방지하는 가드
      if (this.producerIdToConsumer.has(producerId)) {
        console.warn(
          `Consumer for producer ${producerId} already exists. Skipping.`
        );
        return;
      }

      console.log(`     Requesting to consume producer ${producerId}`);
      if (!this.recvTransport) {
        console.warn("recvTransport is not ready, queuing consume request");
        this.pendingConsumeList.push({ producerId, kind });
        return;
      }
      try {
        const data = await this._sendRequest("consume", {
          rtpCapabilities: this.device.rtpCapabilities,
          producerId,
          kind,
        });

        const consumer = await this.recvTransport.consume({
          id: data.id,
          producerId: data.producerId,
          kind: data.kind,
          rtpParameters: data.rtpParameters,
          appData: { ...appData }, // 서버에서 받은 appData를 consumer에 저장
        });
        this.consumers.set(consumer.id, consumer);
        this.producerIdToConsumer.set(producerId, consumer); //    새 맵에 추가
        // peerId를 consumer의 appData에서 가져와 producerIdToPeerIdMap에 저장
        if (appData && appData.peerId) {
          this.producerToPeerIdMap.set(producerId, appData.peerId);
        }

        // UI 매니저가 화면에 그릴 수 있도록 이벤트를 발생시킵니다.
        this.emit("new-consumer", consumer);

        // 4. 생성된 consumer를 즉시 resume하도록 서버에 요청합니다.
        console.log(` Resuming consumer ${consumer.id}`);
        this.ws.send(
          JSON.stringify({
            action: "resumeConsumer",
            data: { consumerId: consumer.id },
          })
        );
      } catch (error) {
        console.error(`    Failed to create consumer for ${producerId}:`, error);
      }
    }

    _handleProducerClosed({ producerId }) {
      console.log(` Producer ${producerId} closed.`);
      const consumer = this.producerIdToConsumer.get(producerId);
      if (consumer) {
        consumer.close();
        this.consumers.delete(consumer.id);
        this.producerIdToConsumer.delete(producerId);
      }

      // producerIdToPeerIdMap에서 peerId를 찾아 제거
      const peerId = this.producerToPeerIdMap.get(producerId);
      if (peerId) {
        this.producerToPeerIdMap.delete(producerId);
      }

      // 화면 공유 프로듀서가 닫혔는지 확인하고, 그렇다면 UI에 알림
      const isScreenShareProducer =
        this.screenProducer && this.screenProducer.id === producerId;
      // 로컬 비디오 프로듀서가 닫혔는지 확인
      const producer = this.producers.get(producerId);
      const isLocalVideoProducer = producer && producer.kind === 'video' && (producer.appData && !producer.appData.source);

      this.emit("producer-closed", { producerId, isScreenShareProducer, isLocalVideoProducer, peerId });
    }
    async _sendRequest(action, data) {
      return new Promise((resolve, reject) => {
        const callbackAction = `${action}Response`;
        this._waitForAction(callbackAction, (response) => {
          if (response.error) {
            reject(new Error(response.error));
          } else {
            resolve(response.data);
          }
        });
        this.ws.send(JSON.stringify({ action, data }));
      });
    }
    //    오디오 트랙을 끄거나 켭니다.
    async setAudioEnabled(enabled) {
      const audioProducer = this._findProducerByKind("audio");
      if (!audioProducer) return;

      if (enabled) {
        await audioProducer.resume();
      } else {
        await audioProducer.pause();
      }
      // 필요하다면 서버에 음소거 상태를 알리는 시그널링을 보낼 수 있습니다.
      // ✅ [추가] 서버에 프로듀서 상태 변경을 알립니다.
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(
          JSON.stringify({
            action: "changeProducerState",
            data: {
              producerId: audioProducer.id,
              kind: "audio",
              action: enabled ? "resume" : "pause",
            },
          })
        );
      }
    }

    //    비디오 트랙을 끄거나 켭니다.
    async setVideoEnabled(enabled) {
      // [수정] 화면 공유가 아닌 '웹캠' 프로듀서를 명확하게 찾습니다.
      const videoProducer = this._findProducerByKind("video", "webcam");
      if (!videoProducer) return;

      if (enabled) {
        await videoProducer.resume();
      } else {
        await videoProducer.pause();
      }

      this.emit("localVideoStateChanged", enabled);

      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(
          JSON.stringify({
            action: "changeProducerState",
            data: {
              producerId: videoProducer.id,
              kind: "video",
              action: enabled ? "resume" : "pause",
              userName: this.userName,
            },
          })
        );
      }
    }
    _findProducerByKind(kind, source) {
      for (const producer of this.producers.values()) {
        if (producer.kind !== kind) {
          continue;
        }

        // source 인자가 없으면 종류만 맞는 첫 번째 프로듀서를 반환 (오디오의 경우)
        if (!source) {
          return producer;
        }

        // source 인자가 있으면 appData.source와 일치하는지 확인 (비디오의 경우)
        const producerSource = producer.appData?.source || "webcam";
        if (producerSource === source) {
          return producer;
        }
      }
      return null;
    }

    //    화면 공유 시작
    async startScreenShare() {
      if (this.screenProducer) {
        console.warn("Screen sharing is already active.");
        return;
      }

      try {
        const stream = await navigator.mediaDevices.getDisplayMedia({
          video: true,
        });
        const track = stream.getVideoTracks()[0];

        this.screenProducer = await this.sendTransport.produce({
          track,
          appData: { source: "screen" },
        });

        // 브라우저의 '공유 중지' 버튼 클릭 감지
        track.onended = () => {
          console.log("Screen sharing stopped by browser button.");
          this.stopScreenShare();
        };

        this.producers.set(this.screenProducer.id, this.screenProducer);
        this.emit("screenShareState", { isSharing: true });
        this.emit("local-screen-share-started", this.screenProducer.track); //    로컬 UI를 위한 이벤트
      } catch (err) {
        console.error("    Failed to start screen sharing:", err);
      }
    }

    //    화면 공유 중지
    async stopScreenShare() {
      if (!this.screenProducer) {
        console.warn("No active screen share to stop.");
        return;
      }

      console.log(" Requesting to stop screen share.");
      // 서버에 화면 공유 중지를 명시적으로 요청
      this.ws.send(
        JSON.stringify({
          action: "stopScreenShare",
          data: { producerId: this.screenProducer.id },
        })
      );

      // 로컬 프로듀서 정리
      const producerId = this.screenProducer.id;
      this.screenProducer.close(); // 스트림을 닫고 'close' 이벤트를 발생시킴
      this.producers.delete(producerId);
      this.screenProducer = null;
      this.emit("screenShareState", { isSharing: false });
      this.emit("local-screen-share-stopped"); //    로컬 UI 정리를 위한 이벤트
    }
  }

  // client/UIManager.js - Modern Zoom-style UI

  const FACE_LANDMARKS_CONNECTORS = [
    { start: 61, end: 146 },
    { start: 146, end: 91 },
    { start: 91, end: 181 },
    { start: 181, end: 84 },
    { start: 84, end: 17 },
    { start: 17, end: 314 },
    { start: 314, end: 405 },
    { start: 405, end: 321 },
    { start: 321, end: 375 },
    { start: 375, end: 291 },
    { start: 61, end: 185 },
    { start: 185, end: 40 },
    { start: 40, end: 39 },
    { start: 39, end: 37 },
    { start: 37, end: 0 },
    { start: 0, end: 267 },
    { start: 267, end: 269 },
    { start: 269, end: 270 },
    { start: 270, end: 409 },
    { start: 409, end: 291 },
    { start: 78, end: 95 },
    { start: 95, end: 88 },
    { start: 88, end: 178 },
    { start: 178, end: 87 },
    { start: 87, end: 14 },
    { start: 14, end: 317 },
    { start: 317, end: 402 },
    { start: 402, end: 318 },
    { start: 318, end: 324 },
    { start: 324, end: 308 },
    { start: 78, end: 191 },
    { start: 191, end: 80 },
    { start: 80, end: 81 },
    { start: 81, end: 82 },
    { start: 82, end: 13 },
    { start: 13, end: 312 },
    { start: 312, end: 311 },
    { start: 311, end: 310 },
    { start: 310, end: 415 },
    { start: 415, end: 308 },
    { start: 362, end: 382 },
    { start: 382, end: 381 },
    { start: 381, end: 380 },
    { start: 380, end: 373 },
    { start: 373, end: 374 },
    { start: 374, end: 390 },
    { start: 390, end: 249 },
    { start: 249, end: 362 },
    { start: 336, end: 296 },
    { start: 296, end: 334 },
    { start: 334, end: 293 },
    { start: 293, end: 300 },
    { start: 300, end: 276 },
    { start: 33, end: 7 },
    { start: 7, end: 163 },
    { start: 163, end: 144 },
    { start: 144, end: 145 },
    { start: 145, end: 153 },
    { start: 153, end: 154 },
    { start: 154, end: 155 },
    { start: 155, end: 33 },
    { start: 107, end: 66 },
    { start: 66, end: 105 },
    { start: 105, end: 63 },
    { start: 63, end: 70 },
    { start: 70, end: 46 },
    { start: 10, end: 338 },
    { start: 338, end: 297 },
    { start: 297, end: 332 },
    { start: 332, end: 284 },
    { start: 284, end: 251 },
    { start: 251, end: 389 },
    { start: 389, end: 356 },
    { start: 356, end: 454 },
    { start: 454, end: 323 },
    { start: 323, end: 361 },
    { start: 361, end: 288 },
    { start: 288, end: 397 },
    { start: 397, end: 365 },
    { start: 365, end: 379 },
    { start: 379, end: 378 },
    { start: 378, end: 400 },
    { start: 400, end: 377 },
    { start: 377, end: 152 },
    { start: 152, end: 148 },
    { start: 148, end: 176 },
    { start: 176, end: 149 },
    { start: 149, end: 150 },
    { start: 150, end: 136 },
    { start: 136, end: 172 },
    { start: 172, end: 58 },
    { start: 58, end: 132 },
    { start: 132, end: 93 },
    { start: 93, end: 234 },
    { start: 234, end: 127 },
    { start: 127, end: 162 },
    { start: 162, end: 21 },
    { start: 21, end: 54 },
    { start: 54, end: 103 },
    { start: 103, end: 67 },
    { start: 67, end: 109 },
    { start: 109, end: 10 },
  ];

  class UIManager {
    constructor() {
      this.isFullScreen = false;
      this.initializeUI();
      this.applyStyles();
    }

    initializeUI() {
      // 웹스퀘어 컨테이너 확인
      const webSquareContainer = document.getElementById('mf_grp_video_area');

      if (webSquareContainer) {
        // 웹스퀘어 모드: 기존 UI 생성하지 않음
        console.log("UIManager: Using WebSquare container mode");
        this.createHeader();
        this.createMainContent();
        this.createControls();
      } else {
        // 기존 모드: 전체 UI 생성
        // Main app container
        this.appRootContainer = document.createElement("div");
        this.appRootContainer.className = "video-conference-app";
        this.appRootContainer.style.cssText = `
        width: 100vw;
        height: 100vh;
        display: flex;
        flex-direction: column;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        overflow: hidden;
        position: relative;
      `;
        document.body.appendChild(this.appRootContainer);

        // Header section
        this.createHeader();

        // Main content area
        this.createMainContent();

        // Controls section
        this.createControls();
      }

      console.log("UIManager: Modern Zoom-style UI created.");
    }

    createHeader() {
      // 웹스퀘어 컨테이너를 우선 찾기
      const webSquareContainer = document.getElementById('mf_grp_session_title');

      if (webSquareContainer) {
        // 웹스퀘어 모드: headerSection을 grp_session_title에 배치
        this.headerSection = document.createElement("div");
        this.headerSection.className = "header-section";
        this.headerSection.style.cssText = `
	           display: flex !important;
	           justify-content: space-between !important;
	           align-items: center !important;
	           padding: 12px 24px !important;
	           background: rgba(255, 255, 255, 0.95) !important;
	           backdrop-filter: blur(10px) !important;
	           border-bottom: 1px solid rgba(255, 255, 255, 0.2) !important;
	           box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1) !important;
	           z-index: 1000 !important;
	           width: 100% !important;
	           height: 60px !important;
	           box-sizing: border-box !important;
	           position: relative !important;
	       `;

        // Room info
        const roomInfo = document.createElement("div");
        roomInfo.className = "room-info";
        roomInfo.style.cssText = `
	           display: flex;
	           align-items: center;
	           gap: 12px;
	           color: #333;
	       `;

        const roomTitle = document.createElement("h2");
        roomTitle.textContent = "ModuLink 화상회의";
        roomTitle.style.cssText = `
	           margin: 0;
	           font-size: 18px;
	           font-weight: 600;
	           color: #2c3e50;
	       `;

        const participantCount = document.createElement("span");
        participantCount.className = "participant-count";
        participantCount.textContent = "참가자 1명";
        participantCount.style.cssText = `
	           background: #e8f4f8;
	           color: #2980b9;
	           padding: 4px 12px;
	           border-radius: 16px;
	           font-size: 12px;
	           font-weight: 500;
	       `;

        roomInfo.appendChild(roomTitle);
        roomInfo.appendChild(participantCount);

        // Header controls
        const headerControls = document.createElement("div");
        headerControls.className = "header-controls";
        headerControls.style.cssText = `
	           display: flex;
	           gap: 8px;
	           align-items: center;
	       `;

        // Fullscreen button
        this.fullscreenButton = this.createHeaderButton("⛶", "전체화면", () => {
          this.toggleFullscreen();
        });

        // Settings button
        this.settingsButton = this.createHeaderButton("⚙", "설정", () => {
          console.log("Settings clicked");
        });

        headerControls.appendChild(this.fullscreenButton);
        headerControls.appendChild(this.settingsButton);

        this.headerSection.appendChild(roomInfo);
        this.headerSection.appendChild(headerControls);

        // 기존 label 숨기기 (createMainContent와 동일한 패턴)
        const label = webSquareContainer.querySelector('.workspace-title');
        if (label) label.style.display = 'none';

        // grp_session_title에 직접 추가
        webSquareContainer.appendChild(this.headerSection);

        console.log("UIManager: Using WebSquare header container mode");

      } else {
        // 폴백: 웹스퀘어 영역이 없으면 기존 방식 사용
        this.headerSection = document.createElement("div");
        this.headerSection.className = "header-section";
        this.headerSection.style.cssText = `
	           display: flex;
	           justify-content: space-between;
	           align-items: center;
	           padding: 12px 24px;
	           background: rgba(255, 255, 255, 0.95);
	           backdrop-filter: blur(10px);
	           border-bottom: 1px solid rgba(255, 255, 255, 0.2);
	           box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
	           z-index: 1000;
	       `;

        // Room info
        const roomInfo = document.createElement("div");
        roomInfo.className = "room-info";
        roomInfo.style.cssText = `
	           display: flex;
	           align-items: center;
	           gap: 12px;
	           color: #333;
	       `;

        const roomTitle = document.createElement("h2");
        roomTitle.textContent = "화상회의";
        roomTitle.style.cssText = `
	           margin: 0;
	           font-size: 18px;
	           font-weight: 600;
	           color: #2c3e50;
	       `;

        const participantCount = document.createElement("span");
        participantCount.className = "participant-count";
        participantCount.textContent = "참가자 1명";
        participantCount.style.cssText = `
	           background: #e8f4f8;
	           color: #2980b9;
	           padding: 4px 12px;
	           border-radius: 16px;
	           font-size: 12px;
	           font-weight: 500;
	       `;

        roomInfo.appendChild(roomTitle);
        roomInfo.appendChild(participantCount);

        // Header controls
        const headerControls = document.createElement("div");
        headerControls.className = "header-controls";
        headerControls.style.cssText = `
	           display: flex;
	           gap: 8px;
	           align-items: center;
	       `;

        // Fullscreen button
        this.fullscreenButton = this.createHeaderButton("⛶", "전체화면", () => {
          this.toggleFullscreen();
        });

        // Settings button
        this.settingsButton = this.createHeaderButton("⚙", "설정", () => {
          console.log("Settings clicked");
        });

        headerControls.appendChild(this.fullscreenButton);
        headerControls.appendChild(this.settingsButton);

        this.headerSection.appendChild(roomInfo);
        this.headerSection.appendChild(headerControls);

        // appRootContainer에 추가 (기존 방식)
        if (this.appRootContainer) {
          this.appRootContainer.appendChild(this.headerSection);
        }

        console.log("UIManager: Using standalone header mode");
      }
    }

    createHeaderButton(icon, tooltip, onClick) {
      const button = document.createElement("button");
      button.innerHTML = icon;
      button.title = tooltip;
      button.onclick = onClick;
      button.style.cssText = `
      width: 36px;
      height: 36px;
      border: none;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 14px;
      color: #555;
      transition: all 0.2s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    `;

      button.addEventListener('mouseenter', () => {
        button.style.background = 'rgba(255, 255, 255, 1)';
        button.style.transform = 'translateY(-1px)';
        button.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
      });

      button.addEventListener('mouseleave', () => {
        button.style.background = 'rgba(255, 255, 255, 0.8)';
        button.style.transform = 'translateY(0)';
        button.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
      });

      return button;
    }

    createMainContent() {
      // 웹스퀘어 컨테이너를 우선 찾기
      const webSquareContainer = document.getElementById('mf_grp_video_area');

      if (webSquareContainer) {
        // 웹스퀘어 모드: 전체 main-content를 grp_video_area에 배치
        this.mainContentArea = document.createElement("div");
        this.mainContentArea.className = "main-content";
        this.mainContentArea.style.cssText = `
            position: absolute !important;
            top: 0 !important;
            left: 0 !important;
            width: 1600px !important;
            height: 665px !important;
            display: flex;
            gap: 16px;
            padding: 16px;
            overflow: hidden;
            z-index: 100;
        `;

        // 기존 label 숨기기
        const label = webSquareContainer.querySelector('.drop-zone-label');
        if (label) label.style.display = 'none';

        // grp_video_area에 직접 추가
        webSquareContainer.appendChild(this.mainContentArea);

      } else {
        // 폴백: 웹스퀘어 영역이 없으면 기존 방식 사용
        this.mainContentArea = document.createElement("div");
        this.mainContentArea.className = "main-content";
        this.mainContentArea.style.cssText = `
        flex: 1;
        display: flex;
        gap: 16px;
        padding: 16px;
        overflow: hidden;
      `;
      }

      // Main video stage
      this.mainStageContainer = document.createElement("div");
      this.mainStageContainer.id = "mainStageContainer";
      this.mainStageContainer.style.cssText = `
        flex: 1;
        position: relative;
        background: #1a1a1a;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        border: 2px solid rgba(255, 255, 255, 0.1);
      `;


      // Video sidebar
      this.sidebarContainer = document.createElement("div");
      this.sidebarContainer.id = "sidebarContainer";
      this.sidebarContainer.style.cssText = `
        width: 280px;
        display: flex;
        flex-direction: column;
        gap: 12px;
        overflow-y: auto;
        padding: 4px;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 12px;
        backdrop-filter: blur(10px);
        scrollbar-width: thin;
        scrollbar-color: rgba(255, 255, 255, 0.3) transparent;
      `;

      // Custom scrollbar for webkit browsers
      const scrollbarStyles = `
      .video-conference-app #sidebarContainer::-webkit-scrollbar {
        width: 6px;
      }
      .video-conference-app #sidebarContainer::-webkit-scrollbar-track {
        background: transparent;
      }
      .video-conference-app #sidebarContainer::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.3);
        border-radius: 3px;
      }
      .video-conference-app #sidebarContainer::-webkit-scrollbar-thumb:hover {
        background: rgba(255, 255, 255, 0.5);
      }
    `;

      if (!document.getElementById('custom-scrollbar-styles')) {
        const style = document.createElement('style');
        style.id = 'custom-scrollbar-styles';
        style.textContent = scrollbarStyles;
        document.head.appendChild(style);
      }

      // 웹스퀘어 사이드바 영역 찾아서 추가
      if (webSquareContainer) {
        // 웹스퀘어 모드: mainContentArea에 함께 배치
        this.mainContentArea.appendChild(this.mainStageContainer);
        this.mainContentArea.appendChild(this.sidebarContainer);
      } else {
        // 기존 모드: 메인 컨텐츠 영역에 함께 배치
        this.mainContentArea.appendChild(this.mainStageContainer);
        this.mainContentArea.appendChild(this.sidebarContainer);
        this.appRootContainer.appendChild(this.mainContentArea);
      }
    }

    createControls() {
      // 웹스퀘어 모드 확인
      const webSquareContainer = document.getElementById('mf_grp_video_area');

      if (webSquareContainer) {
        // 웹스퀘어 모드: 컨트롤을 하단 버튼 영역에 배치
        //const bottomModules = document.getElementById('mf_group11');
        //if (bottomModules) {
        //  // 첫 번째 버튼 영역을 찾아서 컨트롤 버튼들을 배치
        //  const firstButton = bottomModules.children[0];
        //  if (firstButton) {
        //    firstButton.innerHTML = '';
        //    this.createControlButtons(firstButton);
        //	firstButton.style.display = 'none';
        return;
        //  }
        //}
      }

      // 기존 모드 또는 웹스퀘어 영역을 찾지 못한 경우
      this.controlsSection = document.createElement("div");
      this.controlsSection.className = "controls-section";
      this.controlsSection.style.cssText = `
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 20px;
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-top: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 -2px 20px rgba(0, 0, 0, 0.1);
    `;

      this.controlsGroup = document.createElement("div");
      this.controlsGroup.className = "controls-group";
      this.controlsGroup.style.cssText = `
      display: flex;
      gap: 16px;
      align-items: center;
      background: rgba(255, 255, 255, 0.8);
      padding: 12px 24px;
      border-radius: 48px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
      backdrop-filter: blur(10px);
    `;

      this.createControlButtons(this.controlsGroup);

      this.controlsSection.appendChild(this.controlsGroup);

      if (this.appRootContainer) {
        this.appRootContainer.appendChild(this.controlsSection);
      } else {
        document.body.appendChild(this.controlsSection);
      }
    }

    createControlButtons(container) {
      // Create control buttons
      this.muteButton = this.createControlButton(`<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 352 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M176 352c53 0 96-43 96-96V96c0-53-43-96-96-96S80 43 80 96v160c0 53 43 96 96 96zm160-160h-16c-8.8 0-16 7.2-16 16v48c0 74.8-64.5 134.8-140.8 127.4C96.7 376.9 48 317.1 48 250.3V208c0-8.8-7.2-16-16-16H16c-8.8 0-16 7.2-16 16v40.2c0 89.6 64 169.6 152 181.7V464H96c-8.8 0-16 7.2-16 16v16c0 8.8 7.2 16 16 16h160c8.8 0 16-7.2 16-16v-16c0-8.8-7.2-16-16-16h-56v-33.8C285.7 418.5 352 344.9 352 256v-48c0-8.8-7.2-16-16-16z"/></svg>`, "음소거", "audio", true);
      this.cameraOffButton = this.createControlButton(`<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 640 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M633.8 458.1l-55-42.5c15.4-1.4 29.2-13.7 29.2-31.1v-257c0-25.5-29.1-40.4-50.4-25.8L448 177.3v137.2l-32-24.7v-178c0-26.4-21.4-47.8-47.8-47.8H123.9L45.5 3.4C38.5-2 28.5-.8 23 6.2L3.4 31.4c-5.4 7-4.2 17 2.8 22.4L42.7 82 416 370.6l178.5 138c7 5.4 17 4.2 22.5-2.8l19.6-25.3c5.5-6.9 4.2-17-2.8-22.4zM32 400.2c0 26.4 21.4 47.8 47.8 47.8h288.4c11.2 0 21.4-4 29.6-10.5L32 154.7v245.5z"/></svg>`, "카메라 끄기", "video", true);
      this.screenShareButton = this.createControlButton(`< svg xmlns = "http://www.w3.org/2000/svg" width="28" height="28" viewBox = "0 0 576 512" >< !--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M528 0H48C21.5 0 0 21.5 0 48v320c0 26.5 21.5 48 48 48h192l-16 48h-72c-13.3 0-24 10.7-24 24s10.7 24 24 24h272c13.3 0 24-10.7 24-24s-10.7-24-24-24h-72l-16-48h192c26.5 0 48-21.5 48-48V48c0-26.5-21.5-48-48-48zm-16 352H64V64h448v288z"/></svg>`, "화면공유", "screen", true);
      this.whiteboardButton = this.createControlButton(`<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 512 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M109.5 244l134.6-134.6-44.1-44.1-61.7 61.7a7.9 7.9 0 0 1 -11.2 0l-11.2-11.2c-3.1-3.1-3.1-8.1 0-11.2l61.7-61.7-33.6-33.7C131.5-3.1 111.4-3.1 99 9.3L9.3 99c-12.4 12.4-12.4 32.5 0 44.9l100.2 100.2zm388.5-116.8c18.8-18.8 18.8-49.2 0-67.9l-45.3-45.3c-18.8-18.8-49.2-18.8-68 0l-46 46 113.2 113.2 46-46zM316.1 82.7l-297 297L.3 487.1c-2.5 14.5 10.1 27.1 24.6 24.6l107.5-18.8L429.3 195.9 316.1 82.7zm186.6 285.4l-33.6-33.6-61.7 61.7c-3.1 3.1-8.1 3.1-11.2 0l-11.2-11.2c-3.1-3.1-3.1-8.1 0-11.2l61.7-61.7-44.1-44.1L267.9 402.5l100.2 100.2c12.4 12.4 32.5 12.4 44.9 0l89.7-89.7c12.4-12.4 12.4-32.5 0-44.9z"/></svg>`, "칠판", "whiteboard", false);

      // End call button
      this.endCallButton = this.createControlButton("📞", "통화 종료", "end-call", false);
      this.endCallButton.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
      this.endCallButton.addEventListener('mouseenter', () => {
        this.endCallButton.style.background = 'linear-gradient(135deg, #c0392b, #a93226)';
      });
      this.endCallButton.addEventListener('mouseleave', () => {
        this.endCallButton.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
      });

      // 채팅 버튼 생성
      this.chatButton = document.createElement("button");
      this.chatButton.id = "chatButton";
      this.chatButton.textContent = "채팅";
      this.chatButton.style.display = "none";
      this.chatButton.className = "control-button chat";
      this.chatButton.style.cssText = this.createControlButton("💬", "채팅", "chat", false).style.cssText;

      container.appendChild(this.muteButton);
      container.appendChild(this.cameraOffButton);
      container.appendChild(this.screenShareButton);
      container.appendChild(this.whiteboardButton);
      container.appendChild(this.chatButton);
      container.appendChild(this.endCallButton);
    }

    createControlButton(icon, tooltip, type, disabled = false) {
      const button = document.createElement("button");
      button.innerHTML = icon;
      button.title = tooltip;
      button.disabled = disabled;
      button.className = `control-button ${type}`;

      const baseStyles = `
      width: 56px;
      height: 56px;
      border: none;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 20px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    `;

      if (disabled) {
        button.style.cssText = baseStyles + `
        background: linear-gradient(135deg, #bdc3c7, #95a5a6);
        color: #7f8c8d;
        cursor: not-allowed;
        opacity: 0.6;
      `;
      } else {
        button.style.cssText = baseStyles + `
        background: linear-gradient(135deg, #3498db, #2980b9);
        color: white;
      `;

        button.addEventListener('mouseenter', () => {
          if (!button.disabled) {
            button.style.transform = 'translateY(-2px) scale(1.05)';
            button.style.boxShadow = '0 8px 24px rgba(0, 0, 0, 0.3)';
          }
        });

        button.addEventListener('mouseleave', () => {
          if (!button.disabled) {
            button.style.transform = 'translateY(0) scale(1)';
            button.style.boxShadow = '0 4px 16px rgba(0, 0, 0, 0.2)';
          }
        });

        button.addEventListener('mousedown', () => {
          if (!button.disabled) {
            button.style.transform = 'translateY(0) scale(0.95)';
          }
        });

        button.addEventListener('mouseup', () => {
          if (!button.disabled) {
            button.style.transform = 'translateY(-2px) scale(1.05)';
          }
        });
      }

      return button;
    }

    applyStyles() {
      const globalStyles = `
	  /* 웹스퀘어 영역 내의 동적 컨텐츠 스타일 */
	  #mf_grp_video_area {
	      position: relative !important;
	      overflow: hidden !important;
	  }

	  #mf_grp_video_area .main-content {
	      position: absolute !important;
	      top: 0 !important;
	      left: 185 !important;
	      width: 1152px !important;
	      height: 600px !important;
	      z-index: 100 !important;
	  }

	  #mf_grp_video_area .drop-zone-label {
	      display: none !important;
	  }
	  
      .video-conference-app * {
        box-sizing: border-box;
      }

      .video-conference-app .main-stage-layer {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 16px;
        overflow: hidden;
      }

      .video-conference-app .pinned-video-layer { 
        z-index: 1; 
      }
      
      .video-conference-app .screen-share-layer { 
        z-index: 2; 
      }

      .video-conference-app .canvas-layer {
        z-index: 3;
        background-color: transparent;
        pointer-events: none;
      }

      .video-conference-app .canvas-layer.standalone {
        background-color: #FFFFFF;
      }

      .video-conference-app .canvas-layer .canvas-container,
      .video-conference-app .canvas-layer .canvas-toolbar {
        pointer-events: auto;
      }

      .video-conference-app #mainStageContainer > div,
      .video-conference-app #sidebarContainer > div {
        width: 100%;
        height: 100%;
        background: #000;
        border-radius: 12px;
        overflow: hidden;
        position: relative;
        transition: all 0.3s ease;
        border: 2px solid rgba(255, 255, 255, 0.1);
      }

      .video-conference-app #mainStageContainer > div.canvas-layer {
        background-color: transparent;
      }

      .video-conference-app #mainStageContainer > div.canvas-layer.standalone {
        background-color: #FFFFFF !important;
      }

      .video-conference-app #mainStageContainer > div > video,
      .video-conference-app #sidebarContainer > div > video {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .video-conference-app #sidebarContainer > div {
        aspect-ratio: 16 / 9;
        height: auto;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
      }

      .video-conference-app #sidebarContainer > div:hover {
        border-color: #3498db;
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(52, 152, 219, 0.3);
      }

      .video-conference-app .status-indicator-container {
        position: absolute;
        bottom: 8px;
        right: 8px;
        display: flex;
        gap: 6px;
        z-index: 20;
      }

      .video-conference-app .audio-muted-indicator,
      .video-conference-app .video-paused-indicator {
        width: 28px;
        height: 28px;
        background-color: rgba(255, 255, 255, 0.8);
        border-radius: 50%;
        background-repeat: no-repeat;
        background-position: center;
        backdrop-filter: blur(5px);
        border: 2px solid rgba(255, 255, 255, 0.2);
        transition: all 0.3s ease;
      }

      .video-conference-app .audio-muted-indicator {
        background-image: url('/InsWebApp/images/icons/mic_off.svg');
        background-size: 16px;
      }

      .video-conference-app .video-paused-indicator {
        background-image: url('/InsWebApp/images/icons/camera_off.svg');
        background-size: 18px;
      }

      .video-conference-app div.video-paused::after {
        content: '사용자';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, #2c3e50, #34495e);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 3rem;
        font-weight: 300;
        color: #ecf0f1;
        pointer-events: none;
        z-index: 10;
        backdrop-filter: blur(10px);
      }

      .video-conference-app .user-info {
        position: absolute;
        bottom: 8px;
        left: 8px;
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 500;
        z-index: 15;
        backdrop-filter: blur(5px);
      }

      @media (max-width: 768px) {
        .video-conference-app #sidebarContainer {
          width: 200px;
        }
        
        .video-conference-app .controls-group {
          gap: 12px;
          padding: 8px 16px;
        }
        
        .video-conference-app .control-button {
          width: 48px;
          height: 48px;
          font-size: 18px;
        }
      }
    `;

      if (!document.getElementById('zoom-ui-styles')) {
        const style = document.createElement('style');
        style.id = 'zoom-ui-styles';
        style.textContent = globalStyles;
        document.head.appendChild(style);
      }
    }

    // Getters
    getMainStageContainer() {
      return this.mainStageContainer;
      //return document.getElementById('mf_grp_video_area');
    }

    getSidebarContainer() {
      return this.sidebarContainer;
      //return document.getElementById('mf_group6');
    }

    getRemoteMediaContainer() {
      return this.mainStageContainer;
    }

    // Button management
    showWhiteboardButton() {
      if (this.whiteboardButton) {
        this.whiteboardButton.style.display = "flex";
        this.whiteboardButton.disabled = false;
        this.whiteboardButton.style.background = 'linear-gradient(135deg, #3498db, #2980b9)';
        this.whiteboardButton.style.color = 'white';
        this.whiteboardButton.style.cursor = 'pointer';
        this.whiteboardButton.style.opacity = '1';
      }
    }

    hideWhiteboardButton() {
      if (this.whiteboardButton) {
        this.whiteboardButton.style.display = "none";
      }
    }

    disableScreenSharing() {
      if (this.screenShareButton) {
        this.screenShareButton.disabled = true;
        this.screenShareButton.style.background = 'linear-gradient(135deg, #bdc3c7, #95a5a6)';
        this.screenShareButton.style.color = '#7f8c8d';
        this.screenShareButton.style.cursor = 'not-allowed';
        this.screenShareButton.style.opacity = '0.6';
      }
    }

    showChatButton() {
      if (this.chatButton) {
        this.chatButton.style.display = "inline-block";
      }
    }

    enableControls() {
      console.log("Enabling media controls...");
      if (this.muteButton) {
        this.muteButton.disabled = false;
      }
      if (this.cameraOffButton) {
        this.cameraOffButton.disabled = false;
      }

      // Update button styles
      [this.muteButton, this.cameraOffButton].forEach(button => {
        if (button) {
          button.style.background = 'linear-gradient(135deg, #3498db, #2980b9)';
          button.style.color = 'white';
          button.style.cursor = 'pointer';
          button.style.opacity = '1';
        }
      });
    }

    enableScreenSharing(onClickCallback) {
      console.log("Enabling screen sharing feature...");
      if (this.screenShareButton) {
        this.screenShareButton.disabled = false;
        this.screenShareButton.onclick = onClickCallback;
        this.screenShareButton.style.background = 'linear-gradient(135deg, #3498db, #2980b9)';
        this.screenShareButton.style.color = 'white';
        this.screenShareButton.style.cursor = 'pointer';
        this.screenShareButton.style.opacity = '1';
      }
    }

    // Layout management
    updateVideoLayout(mainStageElements, sidebarElements) {
      const mainStage = this.mainStageContainer;
      const sidebar = this.sidebarContainer;

      // Clear existing content
      mainStage.innerHTML = '';
      sidebar.innerHTML = '';

      // Add elements to main stage
      mainStageElements.forEach(element => {
        mainStage.appendChild(element);
        element.classList.remove('thumbnail');
        element.classList.add('main-stage-video');
      });

      // Add elements to sidebar
      sidebarElements.forEach(element => {
        sidebar.appendChild(element);
        element.classList.add('thumbnail');
        element.classList.remove('main-stage-video');

        // Add user info overlay
        this.addUserInfoOverlay(element);
      });
      // Add elements to sidebar

      // Update participant count
      this.updateParticipantCount(sidebarElements.length + mainStageElements.length - 1);
    }

    addUserInfoOverlay(element) {
      // Remove existing overlay
      const existing = element.querySelector('.user-info');
      if (existing) existing.remove();

      // Add new overlay
      const userInfo = document.createElement('div');
      userInfo.className = 'user-info';
      userInfo.textContent = '사용자';
      element.appendChild(userInfo);
    }

    updateParticipantCount(count) {
      const counter = document.querySelector('.participant-count');
      if (counter) {
        counter.textContent = `참가자 ${count}명`;
      }
    }

    // Media status updates
    updateRemoteAudioStatus(elementWrapper, isMuted) {
      const container = this.ensureStatusContainer(elementWrapper);
      let indicator = container.querySelector('.audio-muted-indicator');

      if (isMuted) {
        if (!indicator) {
          indicator = document.createElement('div');
          indicator.className = 'audio-muted-indicator';
          container.appendChild(indicator);
        }
      } else {
        indicator?.remove();
      }
    }

    updateRemoteVideoStatus(elementWrapper, isPaused, userName = 'User') {
      elementWrapper.classList.toggle('video-paused', isPaused);
      elementWrapper.setAttribute('data-username', userName);

      const container = this.ensureStatusContainer(elementWrapper);
      let indicator = container.querySelector('.video-paused-indicator');

      if (isPaused) {
        if (!indicator) {
          indicator = document.createElement('div');
          indicator.className = 'video-paused-indicator';
          container.appendChild(indicator);
        }
      } else {
        indicator?.remove();
      }
    }

    ensureStatusContainer(elementWrapper) {
      let container = elementWrapper.querySelector('.status-indicator-container');
      if (!container) {
        container = document.createElement('div');
        container.className = 'status-indicator-container';
        elementWrapper.appendChild(container);
      }
      return container;
    }

    updateLocalVideoState(isEnabled) {
      const myContainer = document.querySelector('[id*="peer-container"]');
      if (myContainer) {
        myContainer.classList.toggle('video-paused', !isEnabled);
      }
    }

    // Screen sharing
    updateLayoutForScreenShare(isSharing) {
      const mainStage = this.mainStageContainer;
      if (isSharing) {
        mainStage.style.background = '#000';
      } else {
        mainStage.style.background = '#1a1a1a';
      }
    }

    addLocalScreenShare(track) {
      this.updateLayoutForScreenShare(true);
      const screenShareWrapper = document.createElement("div");
      screenShareWrapper.id = "local-screen-share-wrapper";
      screenShareWrapper.style.cssText = `
      width: 100%;
      height: 100%;
      position: relative;
      border-radius: 16px;
      overflow: hidden;
    `;

      const element = document.createElement(track.kind);
      element.autoplay = true;
      element.playsInline = true;
      element.muted = true;
      element.srcObject = new MediaStream([track]);
      element.style.cssText = `
      width: 100%;
      height: 100%;
      object-fit: contain;
    `;

      screenShareWrapper.appendChild(element);
      this.mainStageContainer.appendChild(screenShareWrapper);
      console.log("Added local screen share to UI.");
    }

    removeLocalScreenShare() {
      const element = document.getElementById("local-screen-share-wrapper");
      if (element) {
        element.remove();
        console.log("Removed local screen share from UI.");
        this.updateLayoutForScreenShare(false); // 레이아웃 복원
      }
    }

    drawFaceMesh(landmarks) {
      this.canvas.width = this.video.videoWidth;
      this.canvas.height = this.video.videoHeight;
      this.canvasCtx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      if (!landmarks) return;

      this.canvasCtx.strokeStyle = "rgba(0, 255, 0, 0.7)";
      this.canvasCtx.lineWidth = 1.5;

      for (const connection of FACE_LANDMARKS_CONNECTORS) {
        const start = landmarks[connection.start];
        const end = landmarks[connection.end];
        if (start && end) {
          this.canvasCtx.beginPath();
          this.canvasCtx.moveTo(
            start.x * this.canvas.width,
            start.y * this.canvas.height
          );
          this.canvasCtx.lineTo(
            end.x * this.canvas.width,
            end.y * this.canvas.height
          );
          this.canvasCtx.stroke();
        }
      }
    }

    // ✅ [추가] drawFaceMesh가 참조할 로컬 비디오와 캔버스를 설정하는 함수
    setLocalMediaElements(videoEl, canvasEl) {
      this.video = videoEl;
      this.canvas = canvasEl;
      this.canvasCtx = canvasEl.getContext("2d");
    }

    updateMuteButton(isMuted) {
      if (!this.muteButton) return;

      if (isMuted) {
        this.muteButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 640 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M633.8 458.1l-157.8-122C488.6 312.1 496 285 496 256v-48c0-8.8-7.2-16-16-16h-16c-8.8 0-16 7.2-16 16v48c0 17.9-4 34.8-10.7 50.2l-26.6-20.5c3.1-9.4 5.3-19.2 5.3-29.7V96c0-53-43-96-96-96s-96 43-96 96v45.4L45.5 3.4C38.5-2.1 28.4-.8 23 6.2L3.4 31.5C-2.1 38.4-.8 48.5 6.2 53.9l588.4 454.7c7 5.4 17 4.2 22.5-2.8l19.6-25.3c5.4-7 4.2-17-2.8-22.5zM400 464h-56v-33.8c11.7-1.6 22.9-4.5 33.7-8.3l-50.1-38.7c-6.7 .4-13.4 .9-20.4 .2-55.9-5.5-98.7-48.6-111.2-101.9L144 241.3v6.9c0 89.6 64 169.6 152 181.7V464h-56c-8.8 0-16 7.2-16 16v16c0 8.8 7.2 16 16 16h160c8.8 0 16-7.2 16-16v-16c0-8.8-7.2-16-16-16z"/></svg>`;
        this.muteButton.classList.add('muted');
      } else {
        this.muteButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 352 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M176 352c53 0 96-43 96-96V96c0-53-43-96-96-96S80 43 80 96v160c0 53 43 96 96 96zm160-160h-16c-8.8 0-16 7.2-16 16v48c0 74.8-64.5 134.8-140.8 127.4C96.7 376.9 48 317.1 48 250.3V208c0-8.8-7.2-16-16-16H16c-8.8 0-16 7.2-16 16v40.2c0 89.6 64 169.6 152 181.7V464H96c-8.8 0-16 7.2-16 16v16c0 8.8 7.2 16 16 16h160c8.8 0 16-7.2 16-16v-16c0-8.8-7.2-16-16-16h-56v-33.8C285.7 418.5 352 344.9 352 256v-48c0-8.8-7.2-16-16-16z"/></svg>`;
        this.muteButton.classList.remove('muted');
      }
    }

    updateCameraButton(isOff) {
      if (!this.cameraOffButton) return;

      if (isOff) {
        this.cameraOffButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M336.2 64H47.8C21.4 64 0 85.4 0 111.8v288.4C0 426.6 21.4 448 47.8 448h288.4c26.4 0 47.8-21.4 47.8-47.8V111.8c0-26.4-21.4-47.8-47.8-47.8zm189.4 37.7L416 177.3v157.4l109.6 75.5c21.2 14.6 50.4-.3 50.4-25.8V127.5c0-25.4-29.1-40.4-50.4-25.8z"/></svg>`;
        this.cameraOffButton.classList.add('muted');
      } else {
        this.cameraOffButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><!--!Font Awesome Free 6.7.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.--><path d="M633.8 458.1l-55-42.5c15.4-1.4 29.2-13.7 29.2-31.1v-257c0-25.5-29.1-40.4-50.4-25.8L448 177.3v137.2l-32-24.7v-178c0-26.4-21.4-47.8-47.8-47.8H123.9L45.5 3.4C38.5-2 28.5-.8 23 6.2L3.4 31.4c-5.4 7-4.2 17 2.8 22.4L42.7 82 416 370.6l178.5 138c7 5.4 17 4.2 22.5-2.8l19.6-25.3c5.5-6.9 4.2-17-2.8-22.4zM32 400.2c0 26.4 21.4 47.8 47.8 47.8h288.4c11.2 0 21.4-4 29.6-10.5L32 154.7v245.5z"/></svg>`;
        this.cameraOffButton.classList.remove('muted');
      }
    }
  }

  // client/main.js


  // ✨ 동적 모듈 로더 함수 (import() 대체)
  function loadModule(url, moduleName) {
    return new Promise((resolve, reject) => {
      // 이미 로드된 모듈인지 확인
      if (window[moduleName]) {
        console.log(`${moduleName} is already loaded.`);
        let Module = window[moduleName];
        if (Module && !Module.prototype) {
          Module = Module[moduleName] || Module.default;
        }
        return resolve(Module);
      }

      console.log(`Loading module: ${moduleName} from ${url}`);
      const script = document.createElement("script");
      script.src = `${url}`; // 서버 절대 경로 사용

      script.onload = () => {
        if (window[moduleName]) {
          console.log(`Successfully loaded module: ${moduleName}`);
          let Module = window[moduleName];
          if (Module && !Module.prototype) {
            Module = Module[moduleName] || Module.default;
          }
          resolve(Module);
        } else {
          reject(new Error(`Module ${moduleName} not found after script load.`));
        }
      };

      script.onerror = () => reject(new Error(`Failed to load script: ${url}`));
      document.head.appendChild(script);
    });
  }

  // 이렇게 하면 app.bundle.js가 로드되는 즉시 window.App이 생성됩니다.
  window.App = {
    RoomClient: RoomClient,
    UIManager: UIManager,
    loadModule: loadModule,
  };

})();
//# sourceMappingURL=app.bundle.js.map