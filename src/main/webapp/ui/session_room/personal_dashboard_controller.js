/**
 * 개인용 집중도 대시보드 컨트롤러
 */
window.PersonalDashboardController = {
    isOpen: false,
    updateInterval: null,
    dashboardElement: null,
    currentUserId: null,
    
    init: function(userId) {
        console.log('[Personal Dashboard] 초기화 시작:', userId);
        
        // userId가 없으면 다시 시도
        if (!userId) {
            setTimeout(() => {
                const retryUserId = window.scwin?.myPeerId || 'local-user';
                console.log('[Personal Dashboard] 재시도 userId:', retryUserId);
                this.init(retryUserId);
            }, 1000);
            return;
        }
        
        this.currentUserId = userId;
        this.createDashboardElement();
        
        // 개인 통계 직접 관리
        this.personalStats = {
            name: '나',
            sessionStartTime: Date.now(),
            absenceCount: 0,
            drowsinessCount: 0,
            totalAbsenceTime: 0,
            totalDrowsinessTime: 0,
            isPresent: true,
            isDrowsy: false,
            lastAbsenceStart: null,
            lastDrowsinessStart: null,
            activityLog: []
        };
        
        // AI 이벤트 직접 수신
        this.setupAIEventListeners();
        
        console.log('[Personal Dashboard] 개인 대시보드 초기화 완료:', userId);
    },
    
    setupAIEventListeners: function() {
        // AI 모듈의 이벤트를 직접 수신
        if (window.scwin && window.scwin.loadModule) {
            // MediaPipe 모듈 이벤트 가로채기
            const originalCreatePopUp = window.scwin.createPopUp;
            window.scwin.createPopUp = (...args) => {
                const [popupId] = args;
                if (popupId === 'drowsinessPopUp') {
                    this.handleDrowsinessDetected();
                } else if (popupId === 'adfPopUp') {
                    this.handleAbsenceDetected();
                }
                return originalCreatePopUp.apply(window.scwin, args);
            };
        }
        
        // WebSocket 메시지 수신
        if (window.addEventListener) {
            window.addEventListener('ai-status-update', (event) => {
                this.handleAIStatusUpdate(event.detail);
            });
        }
        
        // 기존 AI 통계 시스템 이벤트도 수신
        if (window.aiMonitoringStats) {
            window.aiMonitoringStats.on('participantStatusUpdated', (data) => {
                if (data.peerId === this.currentUserId) {
                    this.handleStatsUpdate(data);
                }
            });
        }
    },
    
    handleDrowsinessDetected: function() {
        console.log('[Personal Dashboard] 졸음 감지됨');
        
        if (!this.personalStats.isDrowsy) {
            this.personalStats.isDrowsy = true;
            this.personalStats.lastDrowsinessStart = Date.now();
            this.personalStats.drowsinessCount++;
            
            this.addActivityLog('😴', '졸음 상태 감지');
            this.updateDisplay();
        }
        
        // 3초 후 자동으로 졸음 해제 (실제로는 AI에서 해제 이벤트가 와야 함)
        //setTimeout(() => {
        //    this.handleDrowsinessResolved();
        //}, 3000);
    },
    
    handleDrowsinessResolved: function() {
        console.log('[Personal Dashboard] 졸음 해제됨');
        
        if (this.personalStats.isDrowsy && this.personalStats.lastDrowsinessStart) {
            const duration = Date.now() - this.personalStats.lastDrowsinessStart;
            this.personalStats.totalDrowsinessTime += duration;
            this.personalStats.isDrowsy = false;
            this.personalStats.lastDrowsinessStart = null;
            
            this.addActivityLog('😊', '정상 집중 상태');
            this.updateDisplay();
        }
    },
    
    handleAbsenceDetected: function() {
        console.log('[Personal Dashboard] 자리비움 감지됨');
        
        if (this.personalStats.isPresent) {
            this.personalStats.isPresent = false;
            this.personalStats.lastAbsenceStart = Date.now();
            this.personalStats.absenceCount++;
            
            this.addActivityLog('🚶', '자리 비움 감지');
            this.updateDisplay();
        }
        
        // 5초 후 자동으로 복귀 (실제로는 AI에서 복귀 이벤트가 와야 함)
        //setTimeout(() => {
        //    this.handleAbsenceResolved();
        //}, 5000);
    },
    
    handleAbsenceResolved: function() {
        console.log('[Personal Dashboard] 자리복귀 감지됨');
        
        if (!this.personalStats.isPresent && this.personalStats.lastAbsenceStart) {
            const duration = Date.now() - this.personalStats.lastAbsenceStart;
            this.personalStats.totalAbsenceTime += duration;
            this.personalStats.isPresent = true;
            this.personalStats.lastAbsenceStart = null;
            
            this.addActivityLog('✅', '자리 복귀 감지');
            this.updateDisplay();
        }
    },
    
    addActivityLog: function(icon, text) {
        this.personalStats.activityLog.push({
            icon: icon,
            text: text,
            timestamp: Date.now()
        });
        
        // 최근 10개만 유지
        if (this.personalStats.activityLog.length > 10) {
            this.personalStats.activityLog = this.personalStats.activityLog.slice(-10);
        }
    },
    
    updateDisplay: function() {
        if (!this.isOpen) return;
        
        const currentTime = Date.now();
        const sessionDuration = currentTime - this.personalStats.sessionStartTime;
        
        // 현재 진행 중인 시간 계산
        let currentAbsenceTime = 0;
        let currentDrowsinessTime = 0;
        
        if (!this.personalStats.isPresent && this.personalStats.lastAbsenceStart) {
            currentAbsenceTime = currentTime - this.personalStats.lastAbsenceStart;
        }
        
        if (this.personalStats.isDrowsy && this.personalStats.lastDrowsinessStart) {
            currentDrowsinessTime = currentTime - this.personalStats.lastDrowsinessStart;
        }
        
        const totalAbsenceTime = this.personalStats.totalAbsenceTime + currentAbsenceTime;
        const totalDrowsinessTime = this.personalStats.totalDrowsinessTime + currentDrowsinessTime;
        const focusTime = Math.max(0, sessionDuration - totalAbsenceTime - totalDrowsinessTime);
        const participationRate = Math.max(0, Math.min(100, (focusTime / sessionDuration) * 100));
        
        // UI 업데이트
        this.updateParticipationCircle(Math.round(participationRate));
        this.updateStatusText();
        this.updateStudyStats(focusTime, totalAbsenceTime, totalDrowsinessTime);
        this.updateActivityLogDisplay();
        
        //console.log('[Personal Dashboard] 화면 업데이트:', {
        //    participationRate: Math.round(participationRate),
        //    absenceCount: this.personalStats.absenceCount,
        //    drowsinessCount: this.personalStats.drowsinessCount,
        //    isPresent: this.personalStats.isPresent,
        //    isDrowsy: this.personalStats.isDrowsy
        //});
    },
    
    updateParticipationCircle: function(rate) {
        const rateText = document.getElementById('personalParticipationRate');
        const rateCircle = document.getElementById('personalParticipationCircle');
        
        if (rateText) {
            rateText.textContent = rate + '%';
        }
        
        if (rateCircle) {
            const angle = (rate / 100) * 360;
            const color = this.getParticipationColor(rate);
            rateCircle.style.background = `conic-gradient(${color} 0deg ${angle}deg, #e0e0e0 ${angle}deg 360deg)`;
        }
    },
    
    updateStatusText: function() {
        const statusText = document.getElementById('personalStatusText');
        if (!statusText) return;
        
        let text = '양호한 집중 상태';
        let color = '#4CAF50';
        
        if (!this.personalStats.isPresent) {
            text = '현재 자리비움 상태';
            color = '#f44336';
        } else if (this.personalStats.isDrowsy) {
            text = '현재 졸음 감지 상태';
            color = '#FF9800';
        } else if (this.personalStats.absenceCount >= 3 || this.personalStats.drowsinessCount >= 8) {
            text = '집중력 부족 상태';
            color = '#f44336';
        } else if (this.personalStats.absenceCount >= 2 || this.personalStats.drowsinessCount >= 4) {
            text = '주의 집중 상태';
            color = '#FF9800';
        }
        
        statusText.textContent = text;
        statusText.style.color = color;
    },
    
    updateStudyStats: function(focusTime, absenceTime, drowsinessTime) {
        const studyTime = document.getElementById('personalStudyTime');
        const absenceCount = document.getElementById('personalAbsenceCount');
        const attentionRate = document.getElementById('personalAttentionRate');
        const drowsinessCount = document.getElementById('personalDrowsinessCount');
        
        if (studyTime) {
            const focusMinutes = Math.floor(focusTime / 60000);
            studyTime.textContent = focusMinutes + '분';
            studyTime.style.color = focusMinutes >= 10 ? '#4CAF50' : '#FF9800';
        }
        
        if (absenceCount) {
            absenceCount.textContent = this.personalStats.absenceCount + '회';
            absenceCount.style.color = this.personalStats.absenceCount === 0 ? '#4CAF50' : 
                                      this.personalStats.absenceCount <= 2 ? '#FF9800' : '#f44336';
        }
        
        if (attentionRate) {
            const sessionDuration = Date.now() - this.personalStats.sessionStartTime;
            const rate = Math.round((focusTime / sessionDuration) * 100);
            attentionRate.textContent = rate + '%';
            attentionRate.style.color = this.getParticipationColor(rate);
        }
        
        if (drowsinessCount) {
            drowsinessCount.textContent = this.personalStats.drowsinessCount + '회';
            drowsinessCount.style.color = this.personalStats.drowsinessCount === 0 ? '#4CAF50' : 
                                         this.personalStats.drowsinessCount <= 2 ? '#FF9800' : '#f44336';
        }
    },
    
    updateActivityLogDisplay: function() {
        const logContainer = document.getElementById('personalActivityLog');
        if (!logContainer) return;
        
        if (this.personalStats.activityLog.length === 0) {
            logContainer.innerHTML = '<div style="text-align: center; color: #666; padding: 1rem;">활동 기록이 없습니다.</div>';
            return;
        }
        
        const logHTML = this.personalStats.activityLog.slice().reverse().map(activity => {
            const time = new Date(activity.timestamp).toLocaleTimeString('ko-KR', { 
                hour: '2-digit', 
                minute: '2-digit' 
            });
            
            return `
                <div style="
                    display: flex; align-items: center; gap: 0.5rem;
                    padding: 0.5rem 0; border-bottom: 1px solid #f0f0f0;
                ">
                    <span style="font-size: 1.2rem;">${activity.icon}</span>
                    <div style="flex: 1;">
                        <div style="font-size: 0.9rem; font-weight: 500; color: #333;">
                            ${activity.text}
                        </div>
                        <div style="font-size: 0.8rem; color: #666;">
                            ${time}
                        </div>
                    </div>
                </div>
            `;
        }).join('');
        
        logContainer.innerHTML = logHTML;
    },
    
    createDashboardElement: function() {
        if (document.getElementById('personalDashboardSlide')) {
            return;
        }
        
        const dashboardHTML = `
            <div id="personalDashboardSlide" style="
                position: fixed; top: 0; right: -350px; width: 320px; height: 100vh;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                box-shadow: -4px 0 20px rgba(0,0,0,0.15); z-index: 1000;
                transition: right 0.3s ease-in-out; overflow: hidden;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                display: flex; flex-direction: column; padding: 1rem;
            ">
                <!-- 헤더 -->
                <div style="
                    color: white; display: flex; justify-content: space-between; 
                    align-items: center; margin-bottom: 1.5rem;
                ">
                    <div style="font-size: 1.1rem; font-weight: bold;">
                        🎯 내 집중도
                    </div>
                    <button onclick="window.PersonalDashboardController.close()" style="
                        background: rgba(255,255,255,0.2); border: none; color: white;
                        width: 32px; height: 32px; border-radius: 50%; cursor: pointer;
                        font-size: 1.2rem;
                    ">×</button>
                </div>

                <!-- 실시간 집중도 카드 -->
                <div style="
                    background: white; border-radius: 16px; padding: 1.5rem;
                    margin-bottom: 1rem; text-align: center;
                ">
                    <div style="
                        display: flex; align-items: center; justify-content: center;
                        gap: 0.5rem; margin-bottom: 1rem;
                    ">
                        <span style="font-size: 1.5rem;">🎯</span>
                        <span style="font-size: 1.1rem; font-weight: bold; color: #333;">실시간 집중도</span>
                    </div>
                    
                    <div style="position: relative; margin: 1rem 0;">
                        <div id="personalParticipationCircle" style="
                            width: 100px; height: 100px; border-radius: 50%;
                            background: conic-gradient(#4CAF50 0deg 90deg, #e0e0e0 90deg 360deg);
                            display: flex; align-items: center; justify-content: center;
                            margin: 0 auto; position: relative;
                        ">
                            <div style="
                                width: 75px; height: 75px; border-radius: 50%;
                                background: white; position: absolute;
                            "></div>
                            <div id="personalParticipationRate" style="
                                font-size: 1.4rem; font-weight: bold; color: #333; z-index: 1;
                            ">0%</div>
                        </div>
                    </div>
                    
                    <div id="personalStatusText" style="
                        font-size: 1rem; font-weight: 500; color: #4CAF50;
                        margin-top: 0.5rem;
                    ">양호한 집중 상태</div>
                </div>

                <!-- 행동 분석 로그 카드 -->
                <div style="
                    background: white; border-radius: 16px; padding: 1.5rem;
                    margin-bottom: 1rem; flex: 1; overflow: hidden;
                ">
                    <div style="
                        display: flex; align-items: center; gap: 0.5rem;
                        margin-bottom: 1rem;
                    ">
                        <span style="font-size: 1.2rem;">📋</span>
                        <span style="font-size: 1rem; font-weight: bold; color: #333;">행동 분석 로그</span>
                    </div>
                    
                    <div id="personalActivityLog" style="
                        max-height: 200px; overflow-y: auto;
                    ">
                        <!-- 활동 로그 항목들 -->
                    </div>
                </div>

                <!-- 학습 통계 카드 -->
                <div style="
                    background: white; border-radius: 16px; padding: 1.5rem;
                    margin-bottom: 1rem;
                ">
                    <div style="
                        display: flex; align-items: center; gap: 0.5rem;
                        margin-bottom: 1rem;
                    ">
                        <span style="font-size: 1.2rem;">📊</span>
                        <span style="font-size: 1rem; font-weight: bold; color: #333;">집중 통계</span>
                    </div>
                    
                    <div style="
                        display: grid; grid-template-columns: 1fr 1fr;
                        gap: 1rem;
                    ">
                        <div style="text-align: center;">
                            <div id="personalStudyTime" style="
                                font-size: 1.5rem; font-weight: bold; color: #4CAF50;
                            ">0분</div>
                            <div style="font-size: 0.8rem; color: #666;">연속 집중시간</div>
                        </div>
                        <div style="text-align: center;">
                            <div id="personalAbsenceCount" style="
                                font-size: 1.5rem; font-weight: bold; color: #FF9800;
                            ">0회</div>
                            <div style="font-size: 0.8rem; color: #666;">자리비움</div>
                        </div>
                        <div style="text-align: center;">
                            <div id="personalAttentionRate" style="
                                font-size: 1.5rem; font-weight: bold; color: #4CAF50;
                            ">0%</div>
                            <div style="font-size: 0.8rem; color: #666;">최근 집중율</div>
                        </div>
                        <div style="text-align: center;">
                            <div id="personalDrowsinessCount" style="
                                font-size: 1.5rem; font-weight: bold; color: #f44336;
                            ">0회</div>
                            <div style="font-size: 0.8rem; color: #666;">졸음 감지</div>
                        </div>
                    </div>
                </div>

                <!-- 새로고침 버튼 -->
                <div style="text-align: center; margin-top: 0.5rem;">
                    <button onclick="window.PersonalDashboardController.refresh()" style="
                        background: rgba(255,255,255,0.2); border: none; color: white;
                        padding: 0.5rem 1rem; border-radius: 20px; cursor: pointer;
                        font-size: 0.9rem; font-weight: 500;
                    ">🔄 새로고침</button>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', dashboardHTML);
        this.dashboardElement = document.getElementById('personalDashboardSlide');
        
        // 초기 데이터 로드
        this.updateData();
    },
    
    toggle: function() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    },
    
    open: function() {
        if (!this.dashboardElement) {
            this.init(this.currentUserId);
        }
        
        this.dashboardElement.style.right = '0';
        this.isOpen = true;
        
        // 초기 화면 업데이트
        this.updateDisplay();
        
        // 실시간 업데이트 시작 (더 자주 업데이트)
        this.updateInterval = setInterval(() => {
            this.updateDisplay();
        }, 1000); // 1초마다 업데이트
        
        console.log('[Personal Dashboard] 개인 대시보드 열림 - 실시간 모드');
    },
    
    close: function() {
        if (this.dashboardElement) {
            this.dashboardElement.style.right = '-350px';
        }
        this.isOpen = false;
        
        // 업데이트 중지
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
            this.updateInterval = null;
        }
        
        console.log('[Personal Dashboard] 개인 대시보드 닫힘');
    },
    
    updateData: function() {
        if (!window.aiMonitoringStats || !this.currentUserId) {
            console.log('[Personal Dashboard] AI 통계 시스템 또는 사용자 ID 없음');
            console.log('[Personal Dashboard] aiMonitoringStats:', !!window.aiMonitoringStats);
            console.log('[Personal Dashboard] currentUserId:', this.currentUserId);
            this.showNoDataMessage();
            return;
        }
        
        // 모든 참여자 목록 확인
        const allParticipants = window.aiMonitoringStats.getAllParticipants();
        console.log('[Personal Dashboard] 전체 참여자 목록:', allParticipants);
        console.log('[Personal Dashboard] 찾고 있는 사용자 ID:', this.currentUserId);
        
        const personalStats = window.aiMonitoringStats.getParticipantStats(this.currentUserId);
        if (!personalStats) {
            console.log('[Personal Dashboard] 개인 통계 데이터 없음:', this.currentUserId);
            console.log('[Personal Dashboard] 사용 가능한 참여자 ID들:', allParticipants.map(p => p.peerId));
            
            // 대안: 'local-user' 또는 '나'라는 이름으로 찾기
            const alternativeStats = allParticipants.find(p => p.name === '나' || p.peerId.includes('local'));
            if (alternativeStats) {
                console.log('[Personal Dashboard] 대안 통계 데이터 찾음:', alternativeStats);
                this.currentUserId = alternativeStats.peerId;
                this.updatePersonalStats(alternativeStats);
                this.updateActivityLog(alternativeStats);
                return;
            }
            
            this.showNoDataMessage();
            return;
        }
        
        this.updatePersonalStats(personalStats);
        this.updateActivityLog(personalStats);
        
        console.log('[Personal Dashboard] 개인 데이터 업데이트:', personalStats.name, personalStats.statistics);
    },
    
    showNoDataMessage: function() {
        const logContainer = document.getElementById('personalActivityLog');
        if (logContainer) {
            logContainer.innerHTML = '<div style="text-align: center; color: #666; padding: 1rem;">AI 분석 대기 중...</div>';
        }
        
        const statusText = document.getElementById('personalStatusText');
        if (statusText) {
            statusText.textContent = '데이터 수집 중...';
            statusText.style.color = '#666';
        }
    },
    
    updatePersonalStats: function(stats) {
        const participationRate = stats.statistics?.participationRate || 0;
        const statusLevel = stats.statistics?.statusLevel || 'good';
        
        // 참여도 원형 그래프 업데이트
        const rateText = document.getElementById('personalParticipationRate');
        const rateCircle = document.getElementById('personalParticipationCircle');
        
        if (rateText) {
            rateText.textContent = participationRate + '%';
        }
        
        if (rateCircle) {
            const angle = (participationRate / 100) * 360;
            const color = this.getParticipationColor(participationRate);
            rateCircle.style.background = `conic-gradient(${color} 0deg ${angle}deg, #e0e0e0 ${angle}deg 360deg)`;
        }
        
        // 상태 텍스트 업데이트
        const statusText = document.getElementById('personalStatusText');
        if (statusText) {
            const statusInfo = this.getStatusInfo(statusLevel, stats.currentStatus);
            statusText.textContent = statusInfo.text;
            statusText.style.color = statusInfo.color;
        }
        
        // 학습 통계 업데이트
        const studyTime = document.getElementById('personalStudyTime');
        const absenceCount = document.getElementById('personalAbsenceCount');
        const attentionRate = document.getElementById('personalAttentionRate');
        const drowsinessCount = document.getElementById('personalDrowsinessCount');
        
        if (studyTime) {
            // 연속 집중시간 = 전체 세션 시간 - 자리비움 시간 - 수면 시간
            const totalSessionTime = stats.statistics?.sessionDuration || 0;
            const totalAbsenceTime = stats.statistics?.totalAbsenceTime || 0;
            const totalDrowsinessTime = stats.statistics?.totalDrowsinessTime || 0;
            const focusTime = Math.max(0, totalSessionTime - totalAbsenceTime - totalDrowsinessTime);
            
            const focusMinutes = Math.floor(focusTime / 60000);
            studyTime.textContent = focusMinutes + '분';
            
            // 색상 변경 (집중 시간에 따라)
            if (focusMinutes >= 30) {
                studyTime.style.color = '#4CAF50';
            } else if (focusMinutes >= 10) {
                studyTime.style.color = '#8BC34A';
            } else {
                studyTime.style.color = '#FF9800';
            }
        }
        
        if (absenceCount) {
            const count = stats.absenceCount || 0;
            absenceCount.textContent = count + '회';
            
            // 색상 변경 (자리비움 횟수에 따라)
            if (count === 0) {
                absenceCount.style.color = '#4CAF50';
            } else if (count <= 2) {
                absenceCount.style.color = '#FF9800';
            } else {
                absenceCount.style.color = '#f44336';
            }
        }
        
        if (attentionRate) {
            attentionRate.textContent = participationRate + '%';
            attentionRate.style.color = this.getParticipationColor(participationRate);
        }
        
        if (drowsinessCount) {
            const count = stats.drowsinessCount || 0;
            drowsinessCount.textContent = count + '회';
            
            // 색상 변경 (수면 감지 횟수에 따라)
            if (count === 0) {
                drowsinessCount.style.color = '#4CAF50';
            } else if (count <= 2) {
                drowsinessCount.style.color = '#FF9800';
            } else {
                drowsinessCount.style.color = '#f44336';
            }
        }
    },
    
    updateActivityLog: function(stats) {
        const logContainer = document.getElementById('personalActivityLog');
        if (!logContainer || !stats.statusHistory) return;
        
        // 최근 10개 활동만 표시
        const recentActivities = stats.statusHistory.slice(-10).reverse();
        
        if (recentActivities.length === 0) {
            logContainer.innerHTML = '<div style="text-align: center; color: #666; padding: 1rem;">활동 기록이 없습니다.</div>';
            return;
        }
        
        const logHTML = recentActivities.map(activity => {
            const time = new Date(activity.timestamp).toLocaleTimeString('ko-KR', { 
                hour: '2-digit', 
                minute: '2-digit' 
            });
            
            const logInfo = this.getActivityLogInfo(activity.changes);
            
            return `
                <div style="
                    display: flex; align-items: center; gap: 0.5rem;
                    padding: 0.5rem 0; border-bottom: 1px solid #f0f0f0;
                ">
                    <span style="font-size: 1.2rem;">${logInfo.icon}</span>
                    <div style="flex: 1;">
                        <div style="font-size: 0.9rem; font-weight: 500; color: #333;">
                            ${logInfo.text}
                        </div>
                        <div style="font-size: 0.8rem; color: #666;">
                            ${time}
                        </div>
                    </div>
                </div>
            `;
        }).join('');
        
        logContainer.innerHTML = logHTML;
    },
    
    getParticipationColor: function(rate) {
        if (rate >= 90) return '#4CAF50';
        if (rate >= 70) return '#8BC34A';
        if (rate >= 50) return '#FF9800';
        return '#f44336';
    },
    
    getStatusInfo: function(statusLevel, currentStatus) {
        const statusMap = {
            excellent: { text: '우수한 집중 상태', color: '#4CAF50' },
            good: { text: '양호한 집중 상태', color: '#8BC34A' },
            warning: { text: '주의 집중 상태', color: '#FF9800' },
            absent: { text: '집중력 부족 상태', color: '#f44336' }
        };
        
        let baseInfo = statusMap[statusLevel] || statusMap.good;
        
        // 현재 상태에 따른 추가 정보
        if (currentStatus) {
            if (!currentStatus.isPresent) {
                baseInfo = { text: '현재 자리비움 상태', color: '#f44336' };
            } else if (currentStatus.isDrowsy) {
                baseInfo = { text: '현재 졸음 감지 상태', color: '#FF9800' };
            }
        }
        
        return baseInfo;
    },
    
    getActivityLogInfo: function(changes) {
        if (changes.isPresent === false) {
            return { icon: '🚶', text: '자리 비움 감지' };
        } else if (changes.isPresent === true) {
            return { icon: '✅', text: '자리 복귀 감지' };
        } else if (changes.isDrowsy === true) {
            return { icon: '😴', text: '졸음 상태 감지' };
        } else if (changes.isDrowsy === false) {
            return { icon: '😊', text: '정상 집중 상태' };
        }
        return { icon: '📝', text: '상태 업데이트' };
    },
    
    refresh: function() {
        console.log('[Personal Dashboard] 수동 새로고침');
        console.log('[Personal Dashboard] 현재 개인 통계:', this.personalStats);
        
        this.updateDisplay();
        
        // 테스트용 이벤트 시뮬레이션
        console.log('[Personal Dashboard] 테스트 이벤트 시뮬레이션 시작');
        this.handleDrowsinessDetected();
    },
    
    // 테스트용 데이터 업데이트 함수
    testDataUpdate: function() {
        console.log('[Personal Dashboard] 테스트 데이터 업데이트 시작');
        
        // 테스트 이벤트 시뮬레이션
        this.handleDrowsinessDetected();
        
        setTimeout(() => {
            this.handleAbsenceDetected();
        }, 2000);
        
        console.log('[Personal Dashboard] 테스트 데이터 업데이트 완료');
    }
};

// 전역 함수로 테스트 데이터 업데이트 함수 노출
window.testPersonalDashboard = function() {
    window.PersonalDashboardController.testDataUpdate();
};
