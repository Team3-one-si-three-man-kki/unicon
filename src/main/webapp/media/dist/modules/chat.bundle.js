var ChatModule = (function (exports) {
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

  // client/modules/ChatModule.js

  /**
   * ChatModule - 동적 로딩 가능한 채팅 모듈 (CanvasModule 패턴 적용)
   * 기존 chatting.xml의 JavaScript 로직을 클래스로 변환
   */
  class ChatModule extends EventEmitter {
    constructor(container, config = {}) {
      super();
      
      if (!container) {
        throw new Error("Container is required for ChatModule.");
      }
      
      // 기본 설정
      this.container = container;
      this.config = {
        roomId: config.roomId || "test_room_123",
        currentUser: config.currentUser || "사용자",
        websocketManagerUrl: config.websocketManagerUrl || "/InsWebApp/js/websocket/websocket-manager.js",
        ...config
      };
      
      // 채팅 상태 변수들
      this.chatMessages = [];
      this.newMessageCount = 0;
      this.isUserScrolledUp = false;
      
      // DOM 요소들
      this.chatListElement = null;
      this.inputElement = null;
      this.sendButton = null;
      this.newMessageAlert = null;
      
      // WebSocket 관련
      this.isWebSocketReady = false;
      
      console.log('ChatModule 초기화됨:', this.config);

      this._init();
    }

    _init() {
      this._createUI();
      this._injectDependencies(); // 의존성 로드 후 나머지 초기화 진행
    }

    /**
     * 의존성 파일들을 동적으로 로드 (CanvasModule 패턴)
     */
    _injectDependencies() {
      const dependencies = [
        this.config.websocketManagerUrl
      ];

      const loadScript = (src) => {
        return new Promise((resolve, reject) => {
          // 이미 로드된 스크립트인지 확인
          if (document.querySelector(`script[src="${src}"]`)) {
            console.log(`Script already loaded: ${src}`);
            resolve();
            return;
          }
          
          const script = document.createElement("script");
          script.src = src;
          script.onload = () => {
            console.log(`Script loaded successfully: ${src}`);
            resolve();
          };
          script.onerror = () => {
            console.warn(`Failed to load script: ${src}, continuing without it...`);
            resolve(); // 실패해도 계속 진행
          };
          document.head.appendChild(script);
        });
      };

      Promise.all(dependencies.map(loadScript))
        .then(() => {
          console.log("All chat dependencies loaded.");
          
          // 의존성 로드 완료 후 나머지 초기화 실행
          this._attachEventListeners();
          this._initializeWebSocket();
          this._joinRoomAPI();
          
          this.emit("ready");
        })
        .catch((error) => {
          console.warn("Some ChatModule dependencies failed to load:", error);
          // 의존성 실패해도 기본 기능은 동작하도록
          this._attachEventListeners();
          this._joinRoomAPI();
          this.emit("ready");
        });
    }

    /**
     * UI 생성 (CanvasModule 패턴 - CSS와 HTML을 JavaScript에서 생성)
     */
    _createUI() {
      // 스타일 동적 생성 및 추가
      const style = document.createElement("style");
      style.setAttribute("data-module", "chat"); // 나중에 제거하기 쉽도록 속성 추가
      style.textContent = `
        .chat-module-wrap { 
          display: flex; 
          flex-direction: column; 
          height: 100%; 
          background: #fff; 
          border-radius: 8px; 
          overflow: hidden; 
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .chat-module-header { 
          display: flex; 
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px; 
          border-bottom: 1px solid #eee; 
          background: #f8f9fa;
        }
        .chat-module-title {
          font-weight: 600;
          font-size: 14px;
          color: #333;
        }
        .chat-module-close { 
          background: none; 
          border: none; 
          font-size: 18px; 
          cursor: pointer; 
          padding: 4px 8px;
          border-radius: 4px;
          transition: background 0.2s;
        }
        .chat-module-close:hover {
          background: #e9ecef;
        }
        .chat-module-contents { 
          flex: 1; 
          position: relative; 
          overflow: hidden; 
        }
        .chat-module-list { 
          height: 100%; 
          overflow-y: auto; 
          padding: 16px; 
          display: flex; 
          flex-direction: column; 
          gap: 12px; 
        }
        .chat-module-list::-webkit-scrollbar {
          width: 6px;
        }
        .chat-module-list::-webkit-scrollbar-track {
          background: #f1f1f1;
        }
        .chat-module-list::-webkit-scrollbar-thumb {
          background: #c1c1c1;
          border-radius: 3px;
        }
        .chat-module-message { 
          display: flex; 
          flex-direction: column; 
          max-width: 70%; 
        }
        .chat-module-message.user { 
          align-self: flex-end; 
          align-items: flex-end; 
        }
        .chat-module-message.other { 
          align-self: flex-start; 
          align-items: flex-start; 
        }
        .chat-module-message.system {
          align-self: center;
          align-items: center;
          max-width: 90%;
        }
        .chat-module-message .sender { 
          font-size: 11px; 
          color: #666; 
          margin-bottom: 4px; 
          font-weight: 500;
        }
        .chat-module-message .bubble { 
          background: #f1f3f4; 
          padding: 8px 12px; 
          border-radius: 12px; 
          word-wrap: break-word; 
          line-height: 1.4;
          font-size: 14px;
        }
        .chat-module-message.user .bubble { 
          background: #667eea; 
          color: white; 
        }
        .chat-module-message.system .bubble { 
          background: #e8f5e8; 
          color: #2e7d32; 
          text-align: center; 
          font-style: italic; 
          font-size: 12px;
        }
        .chat-module-message .time { 
          font-size: 10px; 
          color: #999; 
          margin-top: 4px; 
        }
        .chat-module-alert { 
          position: absolute; 
          bottom: 80px; 
          left: 50%; 
          transform: translateX(-50%); 
          z-index: 100; 
          display: flex; 
          align-items: center; 
          gap: 8px; 
          background: rgba(102, 126, 234, 0.95); 
          color: white; 
          padding: 8px 12px; 
          border-radius: 20px; 
          box-shadow: 0 2px 10px rgba(0,0,0,0.2); 
          font-size: 12px; 
          animation: chat-slide-up 0.3s ease;
        }
        @keyframes chat-slide-up {
          from { opacity: 0; transform: translateX(-50%) translateY(10px); }
          to { opacity: 1; transform: translateX(-50%) translateY(0); }
        }
        .chat-module-scroll-btn { 
          background: rgba(255,255,255,0.2); 
          border: none; 
          border-radius: 50%; 
          width: 24px; 
          height: 24px; 
          color: white; 
          font-size: 12px; 
          cursor: pointer; 
          display: flex; 
          align-items: center; 
          justify-content: center; 
          transition: background 0.2s;
        }
        .chat-module-scroll-btn:hover {
          background: rgba(255,255,255,0.3);
        }
        .chat-module-footer { 
          border-top: 1px solid #eee; 
          padding: 12px 16px; 
          background: #fff;
        }
        .chat-module-input-group { 
          display: flex; 
          gap: 12px; 
          align-items: center; 
        }
        .chat-module-input-wrap { 
          flex: 1; 
          position: relative; 
        }
        .chat-module-input { 
          width: 100%; 
          min-height: 36px; 
          max-height: 100px; 
          padding: 2px 2px 2px 2px; 
          border: 1px solid #ddd; 
          border-radius: 10px; 
          resize: none; 
          font-family: inherit; 
          font-size: 14px; 
          outline: none; 
          transition: border-color 0.2s;
          line-height: 1.4;
        }
        .chat-module-input:focus { 
          border-color: #667eea; 
        }
        .chat-module-input::placeholder {
          color: #999;
        }
        .chat-module-send-btn { 
          background: #667eea; 
          color: white; 
          border: none; 
          padding: 8px 16px; 
          border-radius: 10px; 
          cursor: pointer; 
          font-weight: 500; 
          font-size: 14px;
          transition: all 0.2s;
          min-width: 60px;
        }
        .chat-module-send-btn:hover { 
          background: #5a6fd8; 
          transform: translateY(-1px);
        }
        .chat-module-send-btn:disabled { 
          background: #ccc; 
          cursor: not-allowed; 
          transform: none;
        }
        .chat-module-typing {
          color: #999;
          font-style: italic;
          font-size: 12px;
          padding: 4px 12px;
          align-self: flex-start;
        }
      `;
      document.head.appendChild(style);

      // HTML 구조 동적 생성
      this.container.innerHTML = `
        <div class="chat-module-wrap">
          <div class="chat-module-contents">
            <div class="chat-module-list"></div>
            <div class="chat-module-alert" style="display: none;">
              <span class="alert-text">새로운 메시지가 있습니다</span>
              <button class="chat-module-scroll-btn" type="button">↓</button>
            </div>
          </div>
          <div class="chat-module-footer">
            <div class="chat-module-input-group">
              <div class="chat-module-input-wrap">
                <textarea class="chat-module-input" placeholder="메시지를 입력하세요..." rows="1"></textarea>
              </div>
              <button class="chat-module-send-btn" type="button">전송</button>
            </div>
          </div>
        </div>
      `;

      // DOM 요소 참조 저장 (CanvasModule 패턴)
      this.chatListElement = this.container.querySelector('.chat-module-list');
      this.inputElement = this.container.querySelector('.chat-module-input');
      this.sendButton = this.container.querySelector('.chat-module-send-btn');
      this.newMessageAlert = this.container.querySelector('.chat-module-alert');
      this.scrollDownButton = this.container.querySelector('.chat-module-scroll-btn');
      this.closeButton = this.container.querySelector('.chat-module-close');
      this.alertText = this.container.querySelector('.alert-text');

      // textarea 자동 크기 조절
      this._setupAutoResize();
    }

    /**
     * textarea 자동 크기 조절 설정
     */
    _setupAutoResize() {
      if (this.inputElement) {
        this.inputElement.addEventListener('input', () => {
          this.inputElement.style.height = 'auto';
          this.inputElement.style.height = Math.min(this.inputElement.scrollHeight, 100) + 'px';
        });
      }
    }

    /**
     * 이벤트 리스너 등록 (CanvasModule 패턴)
     */
    _attachEventListeners() {
      // 전송 버튼 클릭
      if (this.sendButton) {
        this.sendButton.addEventListener('click', () => this.sendMessage());
      }
      
      // 엔터키 처리
      if (this.inputElement) {
        this.inputElement.addEventListener('keypress', (e) => {
          if (e.keyCode === 13 && !e.shiftKey) {
            e.preventDefault();
            this.sendMessage();
          }
        });
      }
      
      // 스크롤 이벤트
      if (this.chatListElement) {
        this.chatListElement.addEventListener('scroll', () => this._onChatScroll());
      }
      
      // 스크롤 다운 버튼
      if (this.scrollDownButton) {
        this.scrollDownButton.addEventListener('click', () => this._scrollToBottomAndHide());
      }
      
      // 닫기 버튼
      if (this.closeButton) {
        this.closeButton.addEventListener('click', () => this.emit('close'));
      }

    }

    /**
     * WebSocket 초기화
     */
    _initializeWebSocket() {
      // UniconWS가 전역에서 사용 가능한지 확인
      try {
        if (typeof UniconWS !== 'undefined' && UniconWS.init) {
          UniconWS.init(this.config.roomId, this.config.currentUser);
          
          // 채팅 메시지 리스너 등록
          if (typeof UniconWS.addListener === 'function') {
            UniconWS.addListener('chat', (data) => {
              console.log('채팅 메시지 수신:', data);
              this.addMessage("other", data.message, data.sender);
              this.emit('messageReceived', data);
            });
          }
          
          this.isWebSocketReady = true;
          console.log('WebSocket 초기화 완료');
        } else {
          console.warn('UniconWS가 로드되지 않음. WebSocket 기능이 제한됩니다.');
          this.isWebSocketReady = false;
        }
      } catch (error) {
        console.warn('WebSocket 초기화 실패:', error);
        this.isWebSocketReady = false;
      }
    }

    /**
     * 메시지 전송
     */
    sendMessage() {
      if (!this.inputElement) return;
      
      const message = this.inputElement.value.trim();

      if (message === "") {
        console.warn("빈 메시지는 전송할 수 없습니다.");
        return;
      }

      // 화면에 메시지 표시
      this.addMessage("user", message, this.config.currentUser);

      // ProWorks API 호출 (시뮬레이션)
      this._sendMessageAPI(message);

      // UniconWS로 메시지 전송
      if (this.isWebSocketReady && typeof UniconWS !== 'undefined') {
        try {
          if (typeof UniconWS.send === 'function') {
            UniconWS.send('chat', {
              message: message,
              sender: this.config.currentUser,
              timestamp: this._getCurrentTime()
            });
          } else {
            console.warn('UniconWS.send 메서드를 찾을 수 없습니다.');
          }
        } catch (error) {
          console.warn('WebSocket 메시지 전송 실패:', error);
        }
      } else {
        console.log('WebSocket이 준비되지 않음. 로컬에서만 메시지 처리됩니다.');
      }

      // 입력창 초기화
      this.inputElement.value = "";
      this.inputElement.style.height = 'auto'; // 높이도 초기화
      this.inputElement.focus();

      // 이벤트 발생
      this.emit('messageSent', { message, sender: this.config.currentUser });
    }

    /**
     * 메시지 추가 (화면에 표시)
     */
    addMessage(type, message, sender) {
      const timestamp = this._getCurrentTime();

      const msgObj = {
        type: type,
        message: message,
        sender: sender,
        time: timestamp
      };

      this.chatMessages.push(msgObj);
      this._displayMessage(msgObj);

      if (type === "user" || !this.isUserScrolledUp) {
        this._scrollToBottom();
      } else {
        this.newMessageCount++;
        this._showNewMessageAlert();
      }
    }

    /**
     * 메시지 DOM에 출력
     */
    _displayMessage(msgObj) {
      const messageHtml = this._createMessageHtml(msgObj);
      
      if (this.chatListElement) {
        this.chatListElement.innerHTML += messageHtml;
      }
    }

    /**
     * 메시지 HTML 생성
     */
    _createMessageHtml(msgObj) {
      // XSS 방지를 위한 텍스트 이스케이프
      const escapeHtml = (text) => {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
      };

      const escapedMessage = escapeHtml(msgObj.message);
      const escapedSender = escapeHtml(msgObj.sender);
      const escapedTime = escapeHtml(msgObj.time);

      if (msgObj.type === "user") {
        return `<div class="chat-module-message user">
                    <div class="bubble">${escapedMessage}</div>
                    <div class="time">${escapedTime}</div>
                </div>`;
      } else if (msgObj.type === "other") {
        return `<div class="chat-module-message other">
                    <div class="sender">${escapedSender}</div>
                    <div class="bubble">${escapedMessage}</div>
                    <div class="time">${escapedTime}</div>
                </div>`;
      } else if (msgObj.type === "system") {
        return `<div class="chat-module-message system">
                    <div class="bubble">${escapedMessage}</div>
                    <div class="time">${escapedTime}</div>
                </div>`;
      }
      return "";
    }

    /**
     * 현재 시간 반환
     */
    _getCurrentTime() {
      const now = new Date();
      const hours = now.getHours();
      const minutes = now.getMinutes();
      const ampm = hours >= 12 ? "오후" : "오전";

      const displayHours = hours % 12 || 12;
      const displayMinutes = minutes < 10 ? "0" + minutes : minutes;

      return `${ampm} ${displayHours}:${displayMinutes}`;
    }

    /**
     * 스크롤 제어
     */
    _scrollToBottom() {
      setTimeout(() => {
        if (this.chatListElement) {
          this.chatListElement.scrollTop = this.chatListElement.scrollHeight - this.chatListElement.clientHeight;
          this.isUserScrolledUp = false;
        }
      }, 100);
    }

    /**
     * 스크롤 감지
     */
    _onChatScroll() {
      if (this.chatListElement) {
        const isAtBottom = (this.chatListElement.scrollTop + this.chatListElement.clientHeight) >= 
                          (this.chatListElement.scrollHeight - 50);
        this.isUserScrolledUp = !isAtBottom;
        if (isAtBottom) this._hideNewMessageAlert();
      }
    }

    /**
     * 새 메시지 알림 표시
     */
    _showNewMessageAlert() {
      if (this.newMessageAlert && this.alertText) {
        this.newMessageAlert.style.display = "flex";
        const msg = this.newMessageCount > 1 ?
          `새로운 메시지 ${this.newMessageCount}개` :
          "새로운 메시지가 있습니다";
        this.alertText.textContent = msg;
      }
    }

    /**
     * 새 메시지 알림 숨김
     */
    _hideNewMessageAlert() {
      if (this.newMessageAlert) {
        this.newMessageAlert.style.display = "none";
        this.newMessageCount = 0;
      }
    }

    /**
     * 스크롤 다운 및 알림 숨김
     */
    _scrollToBottomAndHide() {
      this._scrollToBottom();
      this._hideNewMessageAlert();
      this.isUserScrolledUp = false;
    }

    /**
     * ProWorks API - 채팅방 입장 (시뮬레이션)
     */
    _joinRoomAPI() {
      console.log('채팅방 입장 API 호출:', this.config.roomId);
      
      // 시스템 메시지 추가
      setTimeout(() => {
        this.addMessage("system", "채팅방에 입장했습니다.", "시스템");
      }, 500);
      
      // 이벤트 발생
      this.emit('roomJoined', { roomId: this.config.roomId });
    }

    /**
     * ProWorks API - 메시지 전송 (시뮬레이션)
     */
    _sendMessageAPI(message) {
      console.log('메시지 전송 API 호출:', message);
      
      // 실제 ProWorks API 호출은 추후 구현
      // 현재는 성공으로 처리
      setTimeout(() => {
        this.emit('messageApiSuccess', { message });
      }, 100);
    }

    /**
     * 모듈 정리 (CanvasModule 패턴)
     */
    destroy() {
      console.log('ChatModule 정리 중...');
      
      // 이벤트 리스너 제거
      this.removeAllListeners();
      
      // 스타일 제거
      const style = document.querySelector('style[data-module="chat"]');
      if (style) style.remove();
      
      // DOM 정리
      if (this.container) {
        this.container.innerHTML = '';
      }
      
      console.log('ChatModule 정리 완료');
    }

    /**
     * 설정 업데이트
     */
    updateConfig(newConfig) {
      this.config = { ...this.config, ...newConfig };
      console.log('ChatModule 설정 업데이트:', this.config);
      this.emit('configUpdated', this.config);
    }

    /**
     * 채팅 기록 가져오기
     */
    getChatHistory() {
      return [...this.chatMessages];
    }

    /**
     * 채팅 기록 지우기
     */
    clearChatHistory() {
      this.chatMessages = [];
      if (this.chatListElement) {
        this.chatListElement.innerHTML = '';
      }
      this.emit('historyCleared');
    }

    /**
     * 메시지 개수 가져오기
     */
    getMessageCount() {
      return this.chatMessages.length;
    }

    /**
     * 연결 상태 확인
     */
    isConnected() {
      return this.isWebSocketReady;
    }

    /**
     * 타이핑 인디케이터 표시
     */
    showTypingIndicator(username) {
      // 기존 타이핑 인디케이터 제거
      this.hideTypingIndicator();
      
      const typingHtml = `<div class="chat-module-typing" id="typing-indicator">
                            ${username}님이 입력 중...
                          </div>`;
      
      if (this.chatListElement) {
        this.chatListElement.innerHTML += typingHtml;
        this._scrollToBottom();
      }
    }

    /**
     * 타이핑 인디케이터 숨김
     */
    hideTypingIndicator() {
      const typingIndicator = document.getElementById('typing-indicator');
      if (typingIndicator) {
        typingIndicator.remove();
      }
    }
  }

  exports.ChatModule = ChatModule;

  return exports;

})({});
//# sourceMappingURL=chat.bundle.js.map