/**
 * AI 모니터링 대시보드 컨트롤러
 */
window.AIDashboardController = {
    isOpen: false,
    updateInterval: null,
    dashboardElement: null,
    
    init: function() {
        this.createDashboardElement();
        
        // AI 통계 시스템 이벤트 리스너 등록
        if (window.aiMonitoringStats) {
            window.aiMonitoringStats.on('overallStatsUpdated', (stats) => {
                if (this.isOpen) {
                    this.updateOverallStats(stats);
                }
            });
            
            window.aiMonitoringStats.on('participantStatusUpdated', (data) => {
                if (this.isOpen) {
                    // 전체 참여자 목록 다시 로드
                    const participants = window.aiMonitoringStats.getAllParticipants();
                    this.updateParticipantsList(participants);
                }
            });
        }
        
        console.log('[AI Dashboard] 컨트롤러 초기화 완료');
    },
    
    createDashboardElement: function() {
        if (document.getElementById('aiDashboardSlide')) {
            return;
        }
        
        const dashboardHTML = `
            <div id="aiDashboardSlide" style="
                position: fixed; top: 0; left: -400px; width: 380px; height: 100vh;
                background: white; box-shadow: 4px 0 20px rgba(0,0,0,0.15); z-index: 1000;
                transition: left 0.3s ease-in-out; overflow: hidden;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                display: flex; flex-direction: column;
            ">
                <div style="
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white; padding: 1rem; display: flex;
                    justify-content: space-between; align-items: center;
                ">
                    <div style="font-size: 1.1rem; font-weight: bold;">
                        📊 실시간 집중도 모니터링
                    </div>
                    <button onclick="window.AIDashboardController.close()" style="
                        background: rgba(255,255,255,0.2); border: none; color: white;
                        width: 32px; height: 32px; border-radius: 50%; cursor: pointer;
                        font-size: 1.2rem;
                    ">×</button>
                </div>

                <div style="background: #f8f9fa; padding: 1rem; border-bottom: 1px solid #e1e5e9;">
                    <div style="font-size: 0.9rem; color: #666; margin-bottom: 0.75rem; text-align: center;">
                        전체 평균 집중도
                    </div>
                    
                    <div style="text-align: center; margin-bottom: 1rem;">
                        <div id="participationCircle" style="
                            width: 80px; height: 80px; border-radius: 50%;
                            background: conic-gradient(#4CAF50 0deg 280deg, #e0e0e0 280deg 360deg);
                            display: flex; align-items: center; justify-content: center;
                            margin: 0 auto 0.5rem; position: relative;
                        ">
                            <div style="
                                width: 60px; height: 60px; border-radius: 50%;
                                background: white; position: absolute;
                            "></div>
                            <div id="averageParticipationRate" style="
                                font-size: 1.2rem; font-weight: bold; color: #333; z-index: 1;
                            ">0%</div>
                        </div>
                        <div style="font-size: 0.8rem; color: #666; text-align: center; margin-top: 0.5rem;" class="status-summary">
                            참여자 정보 로딩 중...
                        </div>
                    </div>

                    <div style="
                        display: grid; grid-template-columns: repeat(4, 1fr);
                        gap: 0.5rem; text-align: center;
                    ">
                        <div style="padding: 0.5rem; border-radius: 6px; font-size: 0.8rem; background: #e8f5e8; color: #2e7d2e;">
                            <div>우수</div>
                            <div id="status-excellent">0</div>
                        </div>
                        <div style="padding: 0.5rem; border-radius: 6px; font-size: 0.8rem; background: #e3f2fd; color: #1976d2;">
                            <div>양호</div>
                            <div id="status-good">0</div>
                        </div>
                        <div style="padding: 0.5rem; border-radius: 6px; font-size: 0.8rem; background: #fff3e0; color: #f57c00;">
                            <div>주의</div>
                            <div id="status-warning">0</div>
                        </div>
                        <div style="padding: 0.5rem; border-radius: 6px; font-size: 0.8rem; background: #ffebee; color: #d32f2f;">
                            <div>자리비움</div>
                            <div id="status-absent">0</div>
                        </div>
                    </div>
                </div>

                <div style="flex: 1; overflow-y: auto; padding: 1rem;">
                    <div style="
                        font-size: 0.9rem; color: #666; margin-bottom: 1rem;
                        display: flex; justify-content: space-between; align-items: center;
                    ">
                        <span>참여자별 상태</span>
                        <div>
                            <span id="totalParticipants" style="
                                font-size: 0.8rem; background: #e1e5e9;
                                padding: 0.25rem 0.5rem; border-radius: 12px; margin-right: 0.5rem;
                            ">0명</span>
                            <button onclick="window.AIDashboardController.refresh()" style="
                                background: #f5f7fa; border: 1px solid #ddd; padding: 0.25rem;
                                border-radius: 4px; cursor: pointer; font-size: 0.8rem; color: #666;
                            ">🔄</button>
                        </div>
                    </div>
                    
                    <div id="participantsList">
                        <!-- 참여자 목록 -->
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', dashboardHTML);
        this.dashboardElement = document.getElementById('aiDashboardSlide');
        
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
            this.init();
        }
        
        this.dashboardElement.style.left = '0';
        this.isOpen = true;
        
        // 실제 데이터 업데이트 시작
        this.updateData();
        this.updateInterval = setInterval(() => {
            this.updateData();
        }, 2000); // 2초마다 업데이트
        
        console.log('[AI Dashboard] 대시보드 열림');
    },
    
    close: function() {
        if (this.dashboardElement) {
            this.dashboardElement.style.left = '-400px';
        }
        this.isOpen = false;
        
        // 업데이트 중지
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
            this.updateInterval = null;
        }
        
        console.log('[AI Dashboard] 대시보드 닫힘');
    },
    
    updateData: function() {
        if (window.aiMonitoringStats) {
            // 실제 데이터 사용
            const overallStats = window.aiMonitoringStats.getOverallStats();
            const participants = window.aiMonitoringStats.getAllParticipants();
            
            this.updateOverallStats(overallStats);
            this.updateParticipantsList(participants);
            console.log('[AI Dashboard] 실제 데이터 업데이트:', participants.length, '명');
        } else {
            // aiMonitoringStats가 없으면 기본 메시지 표시
            const listContainer = document.getElementById('participantsList');
            if (listContainer) {
                listContainer.innerHTML = '<div style="text-align: center; color: #666; padding: 2rem;">AI 모니터링 시스템이 초기화되지 않았습니다.</div>';
            }
            console.log('[AI Dashboard] aiMonitoringStats 없음');
        }
    },
    
    updateOverallStats: function(stats) {
        const rateText = document.getElementById('averageParticipationRate');
        const rateCircle = document.getElementById('participationCircle');
        
        if (rateText) {
            rateText.textContent = (stats.averageParticipationRate || 0) + '%';
        }
        
        if (rateCircle) {
            const angle = ((stats.averageParticipationRate || 0) / 100) * 360;
            rateCircle.style.background = `conic-gradient(#4CAF50 0deg ${angle}deg, #e0e0e0 ${angle}deg 360deg)`;
        }
        
        // 상태별 카운트 업데이트
        const statusItems = ['excellent', 'good', 'warning', 'absent'];
        statusItems.forEach(status => {
            const element = document.getElementById(`status-${status}`);
            if (element) {
                element.textContent = (stats.statusCounts && stats.statusCounts[status]) || 0;
            }
        });
        
        // 총 참여자 수 업데이트
        const totalElement = document.getElementById('totalParticipants');
        if (totalElement) {
            totalElement.textContent = (stats.totalParticipants || 0) + '명';
        }
        
        // 상태 요약 텍스트 업데이트
        const summaryElement = document.querySelector('.status-summary');
        if (summaryElement) {
            const excellent = stats.statusCounts?.excellent || 0;
            const absent = stats.statusCounts?.absent || 0;
            const warning = stats.statusCounts?.warning || 0;
            const total = stats.totalParticipants || 0;
            
            let summaryText = `${total}명 참여 중`;
            if (excellent > 0) summaryText += ` / ${excellent}명 우수`;
            if (warning > 0) summaryText += ` / ${warning}명 주의`;
            if (absent > 0) summaryText += ` / ${absent}명 자리비움`;
            
            summaryElement.textContent = summaryText;
        }
    },
    
    updateParticipantsList: function(participants) {
        const listContainer = document.getElementById('participantsList');
        if (!listContainer) return;
        
        if (!participants || participants.length === 0) {
            listContainer.innerHTML = '<div style="text-align: center; color: #666; padding: 2rem;">참여자가 없습니다.</div>';
            return;
        }
        
        listContainer.innerHTML = participants.map(participant => 
            this.createParticipantCard(participant)
        ).join('');
    },
    
    createParticipantCard: function(participant) {
        const statusLabels = { excellent: '우수', good: '양호', warning: '주의', absent: '자리비움' };
        const statusColors = {
            excellent: { bg: '#e8f5e8', color: '#2e7d2e' },
            good: { bg: '#e3f2fd', color: '#1976d2' },
            warning: { bg: '#fff3e0', color: '#f57c00' },
            absent: { bg: '#ffebee', color: '#d32f2f' }
        };
        
        // 실제 데이터 구조에 맞춰 수정
        const stats = participant.statistics || {};
        const statusLevel = stats.statusLevel || 'good';
        const statusColor = statusColors[statusLevel];
        const participationRate = stats.participationRate || 0;
        const absenceCount = participant.absenceCount || 0;
        const drowsinessCount = participant.drowsinessCount || 0;
        const absenceMinutes = stats.absenceMinutes || 0;
        const drowsinessMinutes = stats.drowsinessMinutes || 0;
        const name = participant.name || '알 수 없음';
        
        return `
            <div style="
                background: white; border: 1px solid #e1e5e9; border-radius: 8px;
                padding: 1rem; margin-bottom: 0.75rem;
            ">
                <div style="
                    display: flex; justify-content: space-between; align-items: center;
                    margin-bottom: 0.75rem;
                ">
                    <div style="
                        font-weight: 600; color: #333; display: flex;
                        align-items: center; gap: 0.5rem;
                    ">
                        <div style="
                            width: 24px; height: 24px; border-radius: 50%; background: #667eea;
                            color: white; display: flex; align-items: center; justify-content: center;
                            font-size: 0.7rem; font-weight: bold;
                        ">${name.charAt(0)}</div>
                        ${name}
                    </div>
                    <div style="
                        padding: 0.25rem 0.5rem; border-radius: 12px; font-size: 0.7rem;
                        font-weight: 500; background: ${statusColor.bg}; color: ${statusColor.color};
                    ">
                        ${statusLabels[statusLevel]}
                    </div>
                </div>
                
                <div style="
                    display: grid; grid-template-columns: 1fr 1fr;
                    gap: 0.5rem; font-size: 0.8rem;
                ">
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">참여도</span>
                        <span style="font-weight: 500; color: #333;">${participationRate}%</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">자리비움</span>
                        <span style="font-weight: 500; color: #333;">${absenceCount}회</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">수면감지</span>
                        <span style="font-weight: 500; color: #333;">${drowsinessCount}회</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">자리비움시간</span>
                        <span style="font-weight: 500; color: #333;">${absenceMinutes}분</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">수면시간</span>
                        <span style="font-weight: 500; color: #333;">${drowsinessMinutes}분</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span style="color: #666;">현재상태</span>
                        <span style="font-weight: 500; color: ${participant.currentStatus?.isPresent ? '#4CAF50' : '#f44336'};">
                            ${participant.currentStatus?.isPresent ? '집중상태' : '퇴장'}
                            ${participant.currentStatus?.isDrowsy ? ' 😴' : ''}
                        </span>
                    </div>
                </div>
                
                <div style="
                    width: 100%; height: 4px; background: #e0e0e0; border-radius: 2px;
                    margin-top: 0.5rem; overflow: hidden;
                ">
                    <div style="
                        height: 100%; background: linear-gradient(90deg, #4CAF50, #8BC34A);
                        border-radius: 2px; width: ${participationRate}%;
                        transition: width 0.3s ease;
                    "></div>
                </div>
            </div>
        `;
    },
    
    refresh: function() {
        this.updateData();
        console.log('[AI Dashboard] 수동 새로고침');
        
        // 통계 디버그 출력
        if (window.aiMonitoringStats) {
            window.aiMonitoringStats.debugLog();
        }
    }
};