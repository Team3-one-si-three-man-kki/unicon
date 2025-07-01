/**
 * 전역 WebSocket 매니저
 * 모든 모듈에서 공유 사용
 */
window.UniconWS = {
	// 연결 상태
	socket: null,
	isConnected: false,
	roomId: null,
	currentUser: null,

	// 모듈별 리스너들
	listeners: {
		chat: []
		/* 
		,
		notification: [],
		video: [],
		document: [],
		signature: []
		*/
	},

	/**
	 * WebSocket 연결 초기화
	 */
	init: function(roomId, userName) {
		this.roomId = roomId;
		this.currentUser = userName;
		this.connect();
	},

	/**
	 * WebSocket 연결
	 */
	connect: function() {
		if (this.socket && this.isConnected) {
			console.log("이미 WebSocket이 연결되어 있습니다.");
			return;
		}

		var wsUrl = 'ws://localhost:9093/InsWebApp/chat/' + this.roomId;
		console.log("WebSocket 연결 시도:", wsUrl);

		this.socket = new WebSocket(wsUrl);

		// 연결 성공
		this.socket.onopen = () => {
			this.isConnected = true;
			console.log("WebSocket 연결 성공");
			// this.notifyAll('connected', { roomId: this.roomId }); -> 연결 성공시 전체에게 알림 전송. 나중에 처리
		};

		// 메시지 수신
		this.socket.onmessage = (event) => {
			console.log("WebSocket 메시지(타입별) 수신:", event.data);
			try {
				var data = JSON.parse(event.data);
				this.routeMessage(data);
			} catch (e) {
				console.error("메시지 파싱 에러:", e);
			}
		};

		// 연결 종료
		this.socket.onclose = () => {
			this.isConnected = false;
			console.log("WebSocket 연결 종료");
			// this.notifyAll('disconnected', {}); -> 연결 종료시 전체에게 알림 전송. 나중에 처리

			// 3초 후 재연결 시도
			setTimeout(() => {
				this.connect();
			}, 3000);
		};

		// 에러
		this.socket.onerror = (error) => {
			console.error("WebSocket 에러:", error);
			// this.notifyAll('error', { error: error }); -> 연결 에러시 전체에게 알림 전송. 나중에 처리
		};
	},

	/**
	 * 메시지 라우팅 (타입별로 해당 모듈에 전달)
	 */
	routeMessage: function(data) {
		var messageType = data.type;

		switch (messageType) {
			case 'chat':
				this.notifyListeners('chat', data);
				break;
			/*
					case 'notification':
						this.notifyListeners('notification', data);
						break;
					case 'video-signal':
						this.notifyListeners('video', data);
						break;
					case 'document-sync':
						this.notifyListeners('document', data);
						break;
					case 'signature-update':
						this.notifyListeners('signature', data);
						break;
			*/
			default:
				console.log("알 수 없는 메시지 타입:", messageType);
				// 모든 리스너에게 전달
				this.notifyAll('unknown', data);
		}
	},

	/**
	 * 특정 모듈 리스너들에게 알림 -> module에 사용하는 모듈이 들어감.
	 */
	notifyListeners: function(module, data) {
		if (this.listeners[module]) {
			this.listeners[module].forEach(callback => {
				try {
					callback(data);
				} catch (e) {
					console.error("리스너 실행 에러:", e);
				}
			});
		}
	},

	/**
	 * 모든 리스너들에게 알림
	 */
	notifyAll: function(eventType, data) {
		Object.keys(this.listeners).forEach(module => {
			this.listeners[module].forEach(callback => {
				try {
					callback({ eventType: eventType, data: data });
				} catch (e) {
					console.error("전체 알림 에러:", e);
				}
			});
		});
	},

	/**
	 * 모듈별 리스너 등록
	 */
	addListener: function(module, callback) {
		if (!this.listeners[module]) {
			this.listeners[module] = [];
		}
		this.listeners[module].push(callback);
		console.log(`${module} 모듈 리스너 등록`);
	},

	/**
	 * 메시지 전송
	 */
	send: function(type, data) {
		if (!this.socket || !this.isConnected) {
			console.log("WebSocket이 연결되지 않음");
			return false;
		}

		var message = {
			type: type,							// 얘네는 공통적으로 들어가는 요소들
			sender: this.currentUser,
			roomId: this.roomId,
			timestamp: this.getCurrentTime(),
			...data								// 여기에 뭐든 넣어서 사용가능 -> 퀴즈면 question, 투포면 title 같은 데이터를 넣어서 사용가능
		};

		this.socket.send(JSON.stringify(message));
		console.log("WebSocket 메시지 전송:", message);
		return true;
	},

	/**
	 * 현재 시간
	 */
	getCurrentTime: function() {
		var now = new Date();
		var hours = now.getHours();
		var minutes = now.getMinutes();
		var ampm = hours >= 12 ? "오후" : "오전";

		hours = hours % 12 || 12;
		minutes = minutes < 10 ? "0" + minutes : minutes;

		return `${ampm} ${hours}:${minutes}`;
	},

	/**
	 * 연결 해제
	 */
	disconnect: function() {
		if (this.socket) {
			this.socket.close();
			this.socket = null;
			this.isConnected = false;
		}
	}
};