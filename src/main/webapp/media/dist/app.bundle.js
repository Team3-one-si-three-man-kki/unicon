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
  }

  // client/RoomClient.js (최종 완성 버전)

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
        const videoElement = document.getElementById("localVideo");
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

      //    recvTransport가 없으면 대기열에 추가하고, 있으면 바로 consume을 시도합니다.
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
      this.emit("producer-closed", producerId);
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
      // this.sendPeerStatus({ isMuted: !enabled });
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

  // client/modules/MediaPipeModule.js


  // ✅ EventEmitter를 상속받습니다.
  class MediaPipeModule extends EventEmitter {
    constructor(videoElement) {
      super();

      this.videoElement = videoElement;
      this.worker = new Worker("/InsWebApp/media/dist/mediapipe-worker.bundle.js");

      // ✅ 1. 모든 상태와 상수를 클래스의 속성(this)으로 변경합니다.
      this.isDrowsy = false;
      this.isPresent = true;

      this.LEFT_EYE = [33, 160, 158, 133, 153, 144];
      this.RIGHT_EYE = [362, 385, 387, 263, 373, 380];
      this.EAR_THRESH = 0.2;
      this.DROWSY_FRAMES = 10;

      this.closureFrames = 0;
      this.absenceCounter = 0;
      this.ABSENCE_CONSECUTIVE_FRAMES = 15; // 필요에 따라 조정
      this.analysisIntervalId = null; // ✅ AI 분석 루프의 ID를 저장할 변수

      this.worker.onerror = (error) => {
        console.error("❌ MediaPipe Worker 오류:", error);
        this.emit("error", error); // 에러도 이벤트로 외부에 알립니다.
      };

      // ✅ 경쟁 상태(Race Condition)를 피하기 위해 onmessage 핸들러를 생성자에서 설정합니다.
      this.worker.onmessage = (event) => {
        const { type, landmarks } = event.data;
        if (type === "ready") {
          this._startAnalysisLoop();
        } else if (type === "result") {
          this._handleAnalysisResult(landmarks);
        }
      };
    }

    // ✅ AI 분석을 시작하는 메소드
    start() {
      if (this.analysisIntervalId) {
        console.log("AI analysis is already running.");
        return;
      }
      console.log("🚀 Starting AI analysis loop.");
      const AI_ANALYSIS_INTERVAL = 200;
      this.analysisIntervalId = setInterval(async () => {
        if (
          this.worker &&
          this.videoElement.readyState >= 2 &&
          this.videoElement.videoWidth > 0 &&
          this.videoElement.videoHeight > 0
        ) {
          try {
            const imageBitmap = await createImageBitmap(this.videoElement);
            this.worker.postMessage({ imageBitmap }, [imageBitmap]);
          } catch (error) {
            console.error(
              "❌ Error creating ImageBitmap in MediaPipeModule:",
              error
            );
          }
        }
      }, AI_ANALYSIS_INTERVAL);
    }

    // ✅ AI 분석을 중지하는 메소드
    stop() {
      if (!this.analysisIntervalId) {
        console.log("AI analysis is not running.");
        return;
      }
      console.log("🛑 Stopping AI analysis loop.");
      clearInterval(this.analysisIntervalId);
      this.analysisIntervalId = null;
    }

    _startAnalysisLoop() {
      // 이제 이 함수는 start() 메소드에 의해 관리되므로 비워두거나,
      // 초기 자동 시작이 필요하다면 로직을 유지할 수 있습니다.
      // 현재 요구사항에서는 외부에서 제어하므로 비워둡니다.
    }

    _handleAnalysisResult(landmarks) {
      // ✅ 2. 랜드마크 그리기 요청은 이벤트로만 방송합니다.
      this.emit("landmarksUpdate", landmarks);

      const previousIsPresent = this.isPresent;
      const previousIsDrowsy = this.isDrowsy;

      // --- 자리 비움 / 복귀 판단 ---
      if (!landmarks) {
        this.absenceCounter++;
        if (this.absenceCounter > this.ABSENCE_CONSECUTIVE_FRAMES) {
          this.isPresent = false;
        }
      } else {
        this.absenceCounter = 0;
        this.isPresent = true;
      }

      // --- 졸음 판단 (얼굴이 감지된 경우에만) ---
      if (landmarks) {
        const getEAR = (eyeIndices) => {
          const pts = eyeIndices.map((i) => landmarks[i]);
          const d = (a, b) => Math.hypot(a.x - b.x, a.y - b.y);
          return (
            (d(pts[1], pts[5]) + d(pts[2], pts[4])) / (2 * d(pts[0], pts[3]))
          );
        };
        const ear = (getEAR(this.LEFT_EYE) + getEAR(this.RIGHT_EYE)) / 2;

        if (ear < this.EAR_THRESH) {
          this.closureFrames++;
          if (this.closureFrames >= this.DROWSY_FRAMES) {
            this.isDrowsy = true;
          }
        } else {
          this.isDrowsy = false;
          this.closureFrames = 0;
        }
      } else {
        // 얼굴이 없으면 졸음 상태는 아니므로 리셋
        this.isDrowsy = false;
        this.closureFrames = 0;
      }

      // ✅ 3. 상태가 '변경'되었을 때만 이벤트를 방송합니다.
      if (previousIsPresent !== this.isPresent) {
        this.emit("absenceUpdate", { isPresent: this.isPresent });
      }
      if (previousIsDrowsy !== this.isDrowsy) {
        this.emit("drowsinessUpdate", { isDrowsy: this.isDrowsy });
      }
    }
  }

  // client/UIManager.js

  // 이 상수는 그림을 그리는 UIManager가 가지고 있는 것이 더 적합합니다.
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
    // Left eye
    { start: 362, end: 382 },
    { start: 382, end: 381 },
    { start: 381, end: 380 },
    { start: 380, end: 373 },
    { start: 373, end: 374 },
    { start: 374, end: 390 },
    { start: 390, end: 249 },
    { start: 249, end: 362 },
    // Left eyebrow
    { start: 336, end: 296 },
    { start: 296, end: 334 },
    { start: 334, end: 293 },
    { start: 293, end: 300 },
    { start: 300, end: 276 },
    // Right eye
    { start: 33, end: 7 },
    { start: 7, end: 163 },
    { start: 163, end: 144 },
    { start: 144, end: 145 },
    { start: 145, end: 153 },
    { start: 153, end: 154 },
    { start: 154, end: 155 },
    { start: 155, end: 33 },
    // Right eyebrow
    { start: 107, end: 66 },
    { start: 66, end: 105 },
    { start: 105, end: 63 },
    { start: 63, end: 70 },
    { start: 70, end: 46 },
    // Face oval
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
      // 1. 메인 컨테이너 생성 및 body에 추가
      this.appRootContainer = document.createElement("div");
      this.appRootContainer.className = "sub_contents"; // 기존 class 유지
      this.appRootContainer.style.cssText =
        "width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center;";
      document.body.appendChild(this.appRootContainer);

      // 2. localMediaContainer 생성 및 appRootContainer에 추가
      this.localMediaContainer = document.createElement("div");
      this.localMediaContainer.id = "localMediaContainer";
      this.localMediaContainer.style.cssText =
        "position: relative; width: 300px; height: 225px; border: 1px solid #ccc; border-radius: 4px; background-color: #000; margin-bottom: 10px;";
      this.appRootContainer.appendChild(this.localMediaContainer);

      // 3. video 요소 생성 및 localMediaContainer에 추가
      this.video = document.createElement("video");
      this.video.id = "localVideo";
      this.video.controls = true;
      this.video.autoplay = true; // 자동 재생 추가
      this.video.playsInline = true; // iOS에서 인라인 재생
      this.video.style.cssText = "height: 100%; object-fit: cover;";
      this.localMediaContainer.appendChild(this.video);

      // 4. canvas 요소 생성 및 localMediaContainer에 추가
      this.canvas = document.createElement("canvas");
      this.canvas.id = "localCanvas";
      this.canvas.style.cssText =
        "position: absolute; top: 0; left: 0; width: 100%; height: 100%;";
      this.localMediaContainer.appendChild(this.canvas);
      this.canvasCtx = this.canvas.getContext("2d");

      // 5. controls 그룹 생성 및 appRootContainer에 추가
      this.controlsGroup = document.createElement("div");
      this.controlsGroup.className = "controls"; // 기존 class 유지
      this.appRootContainer.appendChild(this.controlsGroup);

      // 6. muteButton 생성 및 controlsGroup에 추가
      this.muteButton = document.createElement("button"); // xf:trigger 대신 button 사용
      this.muteButton.id = "muteButton";
      this.muteButton.textContent = "음소거";
      this.muteButton.disabled = true;
      this.controlsGroup.appendChild(this.muteButton);

      // 7. cameraOffButton 생성 및 controlsGroup에 추가
      this.cameraOffButton = document.createElement("button"); // xf:trigger 대신 button 사용
      this.cameraOffButton.id = "cameraOffButton";
      this.cameraOffButton.textContent = "카메라 끄기";
      this.cameraOffButton.disabled = true;
      this.controlsGroup.appendChild(this.cameraOffButton);

      // 8. screenShareButton 생성 및 controlsGroup에 추가
      this.screenShareButton = document.createElement("button"); // xf:trigger 대신 button 사용
      this.screenShareButton.id = "screenShareButton";
      this.screenShareButton.textContent = "화면공유";
      this.screenShareButton.disabled = true; // 초기에는 비활성화
      this.controlsGroup.appendChild(this.screenShareButton);

      // 9. remoteMediaContainer 생성 및 appRootContainer에 추가
      this.remoteMediaContainer = document.createElement("div");
      this.remoteMediaContainer.id = "remoteMediaContainer";
      this.appRootContainer.appendChild(this.remoteMediaContainer);

      console.log("UIManager: All UI elements created and appended to DOM.");
    }

    //      [핵심 추가] 모든 컨트롤 버튼을 활성화하는 메소드
    enableControls() {
      console.log("🛠️ Enabling media controls...");
      this.muteButton.disabled = false;
      this.cameraOffButton.disabled = false;
      // screenShareButton은 관리자만 활성화되므로 여기서는 처리하지 않음
    }

    //      관리자 여부에 따라 화면 공유 버튼 활성화
    setAdminControls(isAdmin) {
      console.log(`👑 Admin status: ${isAdmin}. Setting controls.`);
      this.screenShareButton.disabled = !isAdmin;
    }

    //      화면 공유 상태에 따라 레이아웃을 변경하는 메소드
    updateLayoutForScreenShare(isSharing) {
      // localMediaContainer는 이미 this.localMediaContainer로 참조됨
      if (isSharing) {
        // 화면 공유 시, 로컬 비디오는 작게 만들고, 원격 컨테이너는 화면 공유에 집중
        this.localMediaContainer.classList.add("small");
        this.remoteMediaContainer.classList.add("screen-sharing-active");
      } else {
        // 화면 공유 종료 시, 원래대로 복원
        this.localMediaContainer.classList.remove("small");
        this.remoteMediaContainer.classList.remove("screen-sharing-active");
      }
    }

    drawFaceMesh(landmarks) {
      // 캔버스 크기를 비디오 크기에 맞춥니다.
      this.canvas.width = this.video.videoWidth;
      this.canvas.height = this.video.videoHeight;
      this.canvasCtx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      if (!landmarks) return;

      // 선 스타일 설정
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

    // 원격 비디오 엘리먼트 생성 등 다른 UI 관련 로직도 여기에 추가...
    addRemoteTrack(track, producerId, appData) {
      if (!this.remoteMediaContainer) {
        console.error(
          "    UIManager.addRemoteTrack: remoteMediaContainer가 유효하지 않습니다. 원격 트랙을 추가할 수 없습니다."
        );
        return;
      }

      // 화면 공유 스트림인 경우 특별 처리
      if (appData && appData.source === "screen") {
        this.updateLayoutForScreenShare(true);
        const screenShareWrapper = document.createElement("div");
        screenShareWrapper.id = `remote-screen-${producerId}`;
        screenShareWrapper.classList.add("screen-share-wrapper");

        const element = document.createElement(track.kind);
        element.autoplay = true;
        element.playsInline = true;
        element.srcObject = new MediaStream([track]);

        screenShareWrapper.appendChild(element);
        // 화면 공유는 보통 컨테이너의 맨 앞에 오도록 prepend 사용
        this.remoteMediaContainer.prepend(screenShareWrapper);
        console.log(`     Added screen share for producer ${producerId}`);
      } else {
        const element = document.createElement(track.kind);
        element.id = `remote-${producerId}`;
        element.autoplay = true;
        element.playsInline = true;
        if (track.kind === "video") {
          element.controls = true;
        }
        element.srcObject = new MediaStream([track]);

        this.remoteMediaContainer.appendChild(element);
        console.log(
          `     Added remote ${track.kind} element for producer ${producerId}`
        );
      }
    }

    removeRemoteTrack(producerId) {
      // 일반 비디오와 화면 공유 엘리먼트를 모두 찾아 제거
      const remoteVideo = document.getElementById(`remote-${producerId}`);
      const screenShare = document.getElementById(`remote-screen-${producerId}`);

      if (remoteVideo) {
        remoteVideo.remove();
        console.log(`     Removed video element for producer ${producerId}`);
      }
      if (screenShare) {
        screenShare.remove();
        console.log(`     Removed screen share for producer ${producerId}`);
        // 화면 공유가 종료되었으므로 레이아웃 복원
        this.updateLayoutForScreenShare(false);
      }
    }

    //      관리자 자신의 화면 공유를 UI에 추가하는 메소드
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
      this.remoteMediaContainer.prepend(screenShareWrapper);
      console.log("     Added local screen share to UI.");
    }

    //      로컬 화면 공유를 UI에서 제거하는 메소드
    removeLocalScreenShare() {
      const element = document.getElementById("local-screen-share-wrapper");
      if (element) {
        element.remove();
        console.log("     Removed local screen share from UI.");
        this.updateLayoutForScreenShare(false); // 레이아웃 복원
      }
    }
  }

  // client/main.js


  // 2. 웹스퀘어의 전역 스코프에서 이 클래스들을 사용할 수 있도록 window 객체에 할당합니다.
  // 이것이 가장 중요한 부분입니다.
  // 2. 웹스퀘어의 전역 스코프에서 이 클래스들을 사용할 수 있도록 window 객체에 할당합니다.
  // 이것이 가장 중요한 부분입니다.
  window.AppClasses = {
    UIManager: UIManager,
    RoomClient: RoomClient,
    MediaPipeModule: MediaPipeModule,
  };

})();
//# sourceMappingURL=app.bundle.js.map
