var MediaPipeModule = (function (exports) {
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

  // client/modules/MediaPipeModule.js


  // ✅ EventEmitter를 상속받습니다.
  class MediaPipeModule extends EventEmitter {
    constructor(videoElement, url) {
      super();

      this.videoElement = videoElement;
      this.worker = new Worker(url);

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
      //this.emit("landmarksUpdate", landmarks);

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
        if (!this.isPresent) {
          this.emit("absenceStarted", { isPresent: this.isPresent });
        } else {
          this.emit("absenceEnded", { isPresent: this.isPresent });
        }
      }
      if (previousIsDrowsy !== this.isDrowsy) {
        if (this.isDrowsy) {
          this.emit("drowsinessDetected", { isDrowsy: this.isDrowsy });
        } else {
          this.emit("drowsinessResolved", { isDrowsy: this.isDrowsy });
        }
      }
    }
  }

  exports.MediaPipeModule = MediaPipeModule;

  return exports;

})({});
//# sourceMappingURL=mediapipe.bundle.js.map
