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
    }

    join(roomId) {
      //    roomId를 인자로 받습니다.
      if (!roomId) {
        throw new Error("roomId is required to join a room");
      }
      //    WebSocket 접속 주소에 roomId를 쿼리 파라미터로 추가합니다.
      // WebSocket 접속 주소를 현재 페이지의 호스트 주소(IP 또는 도메인)를 동적으로 사용하도록 수정합니다.
      // 이렇게 하면 서버 주소가 변경되어도 클라이언트 코드를 수정할 필요가 없습니다.
      // 포트는 3000으로 고정합니다.
      const wsUrl = `wss://${"13.125.229.206:3000"}/?roomId=${roomId}`;
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
            this.emit("adminStatus", this.isAdmin); // UI 매니저에게 알림
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
            const { producerId, kind, state } = msg.data;
            if (state === "pause") {
              if (kind === "video")
                this.emit("remote-producer-pause", { producerId });
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

      this.sendTransport.on(
        "connect",
        ({ dtlsParameters }, callback, errback) => {
          this.ws.send(
            JSON.stringify({
              action: "connectTransport",
              data: { dtlsParameters },
            })
          );
          this._waitForAction("transportConnected", callback);
        }
      );

      this.sendTransport.on(
        "produce",
        async ({ kind, rtpParameters, appData }, callback, errback) => {
          try {
            console.log(`🎬 Producing ${kind}...`);
            // _sendRequest를 사용하여 서버에 produce 요청을 보냅니다.
            const producer = await this._sendRequest("produce", {
              kind,
              rtpParameters,
              appData,
            });
            console.log(
              `   ${kind} production started with server id: ${producer.id}`
            );
            this.producers.set(producer.id, producer); // 실제 producer 객체 저장
            callback({ id: producer.id });
          } catch (error) {
            errback(error);
          }
        }
      );

      await this._startProducing();
    }

    async _startProducing() {
      try {
        this.localStream = await navigator.mediaDevices.getUserMedia({
          video: { width: { ideal: 640 }, height: { ideal: 480 } },
          audio: true,
        });
        const videoElement = this.uiManager.video; // Use the reference from UIManager
        videoElement.srcObject = this.localStream;

        videoElement.oncanplay = () => {
          videoElement.oncanplay = null;
          console.log("   Video element is ready to play.");
          this.emit("localStreamReady", videoElement); // AI 모듈이 videoElement를 참조할 수 있도록 전달

          (async () => {
            const videoTrack = this.localStream.getVideoTracks()[0];
            const audioTrack = this.localStream.getAudioTracks()[0];
            let videoProducer, audioProducer;

            if (videoTrack) {
              videoProducer = await this.sendTransport.produce({
                track: videoTrack,
              });
              this.producers.set(videoProducer.id, videoProducer); // 프로듀서 객체 저장
            }
            if (audioTrack) {
              audioProducer = await this.sendTransport.produce({
                track: audioTrack,
              });
              this.producers.set(audioProducer.id, audioProducer); // 프로듀서 객체 저장
            }
            this.ws.send(JSON.stringify({ action: "deviceReady" }));
            //    [핵심 추가] 모든 produce가 끝난 후, 컨트롤 준비 완료 이벤트를 방송합니다.
            console.log("   All producers created. Controls are now ready.");
            this.emit("controlsReady");
          })();
        };
      } catch (err) {
        console.error("    CRITICAL: Failed to get user media.", err);
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
            resolve(
              response.id ? { id: response.id, ...response.data } : response.data
            );
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
      const videoProducer = this._findProducerByKind("video");
      if (!videoProducer) return;

      if (enabled) {
        await videoProducer.resume();
      } else {
        await videoProducer.pause();
      }
      // 로컬 비디오의 카메라 상태 변경은 UIManager의 전용 함수를 통해 처리
      this.emit("localVideoStateChanged", enabled);

      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(
          JSON.stringify({
            action: "changeProducerState",
            data: {
              producerId: videoProducer.id,
              kind: "video",
              action: enabled ? "resume" : "pause",
            },
          })
        );
        }
      }
      _findProducerByKind(kind) {
        // RoomClient가 관리하는 producers 맵에서 찾습니다.
        for (const producer of this.producers.values()) {
          if (producer.kind === kind) {
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

  // client/UIManager.js

  // UIManager는 더 이상 특정 모듈(CanvasModule)을 알지 못합니다.
  // import { CanvasModule } from "./modules/CanvasModule.js";

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
      this.appRootContainer = document.createElement("div");
      this.appRootContainer.className = "sub_contents";
      this.appRootContainer.style.cssText =
        "width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center;";
      document.body.appendChild(this.appRootContainer);

      this.localMediaContainer = document.createElement("div");
      this.localMediaContainer.id = "localMediaContainer";
      this.localMediaContainer.style.cssText =
        "position: relative; width: 300px; height: 225px; border: 1px solid #ccc; border-radius: 4px; background-color: #000; margin-bottom: 10px;";
      this.appRootContainer.appendChild(this.localMediaContainer); // Ensure local media container is added to the DOM

      this.video = document.createElement("video");
      this.video.id = "localVideo";
      this.video.controls = true;
      this.video.muted = true;
      this.video.autoplay = true;
      this.video.playsInline = true;
      this.video.style.cssText = "height: 100%; object-fit: cover;";
      this.localMediaContainer.appendChild(this.video);

      this.canvas = document.createElement("canvas");
      this.canvas.id = "localCanvas";
      this.canvas.style.cssText =
        "position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none;"; // AI용 캔버스은 이벤트 방해 안함
      this.localMediaContainer.appendChild(this.canvas);
      this.canvasCtx = this.canvas.getContext("2d");

      this.controlsGroup = document.createElement("div");
      this.controlsGroup.className = "controls";
      this.appRootContainer.appendChild(this.controlsGroup);

      this.muteButton = document.createElement("button");
      this.muteButton.id = "muteButton";
      this.muteButton.textContent = "음소거";
      this.muteButton.disabled = true;
      this.controlsGroup.appendChild(this.muteButton);

      this.cameraOffButton = document.createElement("button");
      this.cameraOffButton.id = "cameraOffButton";
      this.cameraOffButton.textContent = "카메라 끄기";
      this.cameraOffButton.disabled = true;
      this.controlsGroup.appendChild(this.cameraOffButton);

      this.screenShareButton = document.createElement("button");
      this.screenShareButton.id = "screenShareButton";
      this.screenShareButton.textContent = "화면공유";
      this.screenShareButton.disabled = true;
      this.controlsGroup.appendChild(this.screenShareButton);

      this.whiteboardButton = document.createElement("button");
      this.whiteboardButton.id = "whiteboardButton";
      this.whiteboardButton.textContent = "칠판";
      this.whiteboardButton.style.display = "none";
      this.controlsGroup.appendChild(this.whiteboardButton);

      // --- 핵심 수정: 새로운 원격 미디어 섹션 ---
      this.remoteSection = document.createElement("div");
      this.remoteSection.id = "remoteSection";
      this.appRootContainer.appendChild(this.remoteSection);

      this.mainStageContainer = document.createElement("div");
      this.mainStageContainer.id = "mainStageContainer";
      this.remoteSection.appendChild(this.mainStageContainer);

      this.sidebarContainer = document.createElement("div");
      this.sidebarContainer.id = "sidebarContainer";
      this.remoteSection.appendChild(this.sidebarContainer);

      console.log("UIManager: Stage and Sidebar UI created.");
    }

    getMainStageContainer() {
      return this.mainStageContainer;
    }
    getSidebarContainer() {
      return this.sidebarContainer;
    }

    // main.js가 공용 컨테이너에 접근할 수 있도록 getter 제공
    getRemoteMediaContainer() {
      // 이 메서드는 더 이상 사용되지 않거나, mainStageContainer를 반환하도록 변경될 수 있습니다.
      // 현재는 remoteMediaContainer가 존재하지 않으므로 mainStageContainer를 반환합니다.
      return this.mainStageContainer;
    }

    // 칠판 버튼을 표시하는 메서드
    showWhiteboardButton() {
      this.whiteboardButton.style.display = "inline-block";
    }

    enableControls() {
      console.log("Enabling media controls...");
      this.muteButton.disabled = false;
      this.cameraOffButton.disabled = false;
    }

    enableScreenSharing(onClickCallback) {
      console.log("Enabling screen sharing feature...");
      this.screenShareButton.disabled = false;
      this.screenShareButton.onclick = onClickCallback;
    }

    updateLayoutForScreenShare(isSharing) {
      if (isSharing) {
        this.localMediaContainer.classList.add("small");
        this.mainStageContainer.classList.add("screen-sharing-active"); // Apply to main stage for layout adjustment
      } else {
        this.localMediaContainer.classList.remove("small");
        this.mainStageContainer.classList.remove("screen-sharing-active"); // Remove from main stage
        this.resetLayoutAfterScreenShare(); // Call new method to reset layout
      }
    }

    resetLayoutAfterScreenShare() {
      console.log("Resetting layout after screen share.");
      // 모든 비디오 요소를 mainStageContainer로 이동
      const allVideoElements = Array.from(document.querySelectorAll("video"));
      allVideoElements.forEach((videoElement) => {
        // localVideo는 localMediaContainer에 유지
        if (videoElement.id === "localVideo") {
          this.localMediaContainer.appendChild(videoElement);
        } else {
          // 다른 모든 비디오는 mainStageContainer로 이동
          this.mainStageContainer.appendChild(videoElement);
        }
      });

      // 로컬 화면 공유 요소가 남아있다면 제거
      const localScreenShareElement = document.getElementById(
        "local-screen-share-wrapper"
      );
      if (localScreenShareElement) {
        localScreenShareElement.remove();
        console.log("Removed local screen share wrapper from UI.");
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

    // 로컬 비디오 상태 업데이트 (카메라 on/off에 따른 아바타 표시)
    updateLocalVideoState(isEnabled) {
      this.localMediaContainer.classList.toggle('video-paused', !isEnabled);
    }

    /**
     * ✅ [최종 수정] 원격 사용자의 오디오 상태를 UI에 업데이트합니다.
     * @param {HTMLElement} elementWrapper - 상태를 표시할 비디오 컨테이너.
     * @param {boolean} isMuted - 음소거 여부.
     */
    updateRemoteAudioStatus(elementWrapper, isMuted) {
      const container = this.ensureStatusContainer(elementWrapper);
      let indicator = container.querySelector('.audio-muted-indicator');

      if (isMuted) {
        // 음소거 상태일 때, 아이콘이 없으면 새로 생성합니다.
        if (!indicator) {
          indicator = document.createElement('div');
          indicator.className = 'audio-muted-indicator';
          container.appendChild(indicator);
        }
      } else {
        // 음소거가 아닐 때, 아이콘이 존재하면 제거합니다.
        indicator?.remove();
      }
    }

    /**
     * ✅ [최종 수정] 원격 사용자의 비디오 상태를 UI에 업데이트합니다.
     * 'video-paused' 클래스 토글과 아이콘 표시를 함께 관리합니다.
     * @param {HTMLElement} elementWrapper - 상태를 표시할 비디오 컨테이너.
     * @param {boolean} isPaused - 비디오 중지 여부.
     */
    updateRemoteVideoStatus(elementWrapper, isPaused) {
      // 아바타 표시를 위한 클래스 토글
      elementWrapper.classList.toggle('video-paused', isPaused);

      const container = this.ensureStatusContainer(elementWrapper);
      let indicator = container.querySelector('.video-paused-indicator');

      if (isPaused) {
        // 비디오가 꺼졌을 때, 아이콘이 없으면 새로 생성합니다.
        if (!indicator) {
          indicator = document.createElement('div');
          indicator.className = 'video-paused-indicator';
          container.appendChild(indicator);
        }
      } else {
        // 비디오가 켜졌을 때, 아이콘이 존재하면 제거합니다.
        indicator?.remove();
      }
    }

    /**
     * ✅ [최종 수정] elementWrapper 내부에 상태 아이콘 컨테이너가 있는지 확인하고 없으면 생성합니다.
     * @param {HTMLElement} elementWrapper
     * @returns {HTMLElement} 상태 아이콘 컨테이너
     */
    ensureStatusContainer(elementWrapper) {
      let container = elementWrapper.querySelector('.status-indicator-container');
      if (!container) {
        container = document.createElement('div');
        container.className = 'status-indicator-container';
        elementWrapper.appendChild(container);
      }
      return container;
    }

    // 비디오 레이아웃 업데이트 (DOM 조작 최소화)
    updateVideoLayout(mainStageElements, sidebarElements) {
      const mainStage = this.mainStageContainer;
      const sidebar = this.sidebarContainer;

      // 현재 DOM 상태를 파악
      const currentMainChildren = Array.from(mainStage.children);
      const currentSidebarChildren = Array.from(sidebar.children);

      // 1. 메인 스테이지에 있어야 할 요소들을 처리
      mainStageElements.forEach(element => {
        if (element.parentNode !== mainStage) {
          mainStage.appendChild(element);
        }
        element.classList.remove('thumbnail');
        element.classList.add('main-stage-video');
      });

      // 2. 사이드바에 있어야 할 요소들을 처리
      sidebarElements.forEach(element => {
        if (element.parentNode !== sidebar) {
          sidebar.appendChild(element);
        }
        element.classList.add('thumbnail');
        element.classList.remove('main-stage-video');
      });

      // 3. 더 이상 메인 스테이지나 사이드바에 속하지 않는 요소들을 제거 (필요시)
      // 이 로직은 main.js에서 직접 요소를 관리하므로 여기서는 필요 없을 수 있습니다.
      // 하지만 혹시 모를 잔여 요소 정리를 위해 남겨둡니다.
      currentMainChildren.forEach(child => {
        if (!mainStageElements.includes(child) && !sidebarElements.includes(child)) ;
      });

      currentSidebarChildren.forEach(child => {
        if (!mainStageElements.includes(child) && !sidebarElements.includes(child)) ;
      });
    }

    addLocalScreenShare(track) {
      this.updateLayoutForScreenShare(true);
      const screenShareWrapper = document.createElement("div");
      screenShareWrapper.id = "local-screen-share-wrapper"; // 로컬 공유는 ID가 고정됨
      screenShareWrapper.classList.add("screen-share-wrapper");

      const element = document.createElement(track.kind);
      element.autoplay = true;
      element.playsInline = true;
      element.muted = true; // 자기 자신의 소리는 음소거
      element.srcObject = new MediaStream([track]);

      screenShareWrapper.appendChild(element);
      this.mainStageContainer.prepend(screenShareWrapper); // remoteMediaContainer 대신 mainStageContainer 사용
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
