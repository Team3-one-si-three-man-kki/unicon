/**
 * AI 모니터링 통계 관리 클래스
 * 세션 시작부터 참여자들의 자리비움/수면 상태를 누적하여 통계를 관리합니다.
 */
class AIMonitoringStats {
    constructor() {
        this.sessionStartTime = Date.now();
        this.participants = new Map(); // peerId -> 참여자 통계 데이터
        this.eventListeners = new Map(); // 이벤트 리스너들
    }

    /**
     * 참여자 초기화
     * @param {string} peerId - 참여자 ID
     * @param {string} peerName - 참여자 이름 (옵션)
     */
    initializeParticipant(peerId, peerName = null) {
        if (!this.participants.has(peerId)) {
            this.participants.set(peerId, {
                peerId: peerId,
                name: peerName || `참여자 ${this.participants.size + 1}`,
                joinTime: Date.now(),
                
                // 현재 상태
                currentStatus: {
                    isPresent: true,
                    isDrowsy: false,
                    lastUpdate: Date.now()
                },
                
                // 카운터
                absenceCount: 0,        // 자리비움 횟수
                drowsinessCount: 0,     // 수면 횟수
                
                // 시간 추적
                absenceStartTime: null,     // 현재 자리비움 시작 시간
                drowsinessStartTime: null,  // 현재 수면 시작 시간
                totalAbsenceTime: 0,        // 총 자리비움 시간 (밀리초)
                totalDrowsinessTime: 0,     // 총 수면 시간 (밀리초)
                
                // 상태 히스토리 (디버깅/분석용)
                statusHistory: []
            });
            
            console.log(`[AI Stats] 참여자 초기화: ${peerId} (${peerName})`);
        }
    }

    /**
     * 참여자 상태 업데이트
     * @param {string} peerId - 참여자 ID
     * @param {Object} newStatus - 새로운 상태 {isPresent?, isDrowsy?}
     */
    updateParticipantStatus(peerId, newStatus) {
        if (!this.participants.has(peerId)) {
            this.initializeParticipant(peerId);
        }

        const participant = this.participants.get(peerId);
        const currentTime = Date.now();
        const previousStatus = { ...participant.currentStatus };

        // 자리비움 상태 변화 처리
        if (newStatus.hasOwnProperty('isPresent')) {
            const wasPresent = participant.currentStatus.isPresent;
            const isNowPresent = newStatus.isPresent;

            if (wasPresent && !isNowPresent) {
                // 자리비움 시작
                participant.absenceStartTime = currentTime;
                participant.absenceCount++;
                console.log(`[AI Stats] ${participant.name} 자리비움 시작 (${participant.absenceCount}번째)`);
                
            } else if (!wasPresent && isNowPresent) {
                // 자리비움 종료
                if (participant.absenceStartTime) {
                    const absenceDuration = currentTime - participant.absenceStartTime;
                    participant.totalAbsenceTime += absenceDuration;
                    participant.absenceStartTime = null;
                    console.log(`[AI Stats] ${participant.name} 자리비움 종료 (지속시간: ${this.formatDuration(absenceDuration)})`);
                }
            }

            participant.currentStatus.isPresent = isNowPresent;
        }

        // 수면 상태 변화 처리
        if (newStatus.hasOwnProperty('isDrowsy')) {
            const wasDrowsy = participant.currentStatus.isDrowsy;
            const isNowDrowsy = newStatus.isDrowsy;

            if (!wasDrowsy && isNowDrowsy) {
                // 수면 시작
                participant.drowsinessStartTime = currentTime;
                participant.drowsinessCount++;
                console.log(`[AI Stats] ${participant.name} 수면 시작 (${participant.drowsinessCount}번째)`);
                
            } else if (wasDrowsy && !isNowDrowsy) {
                // 수면 종료
                if (participant.drowsinessStartTime) {
                    const drowsinessDuration = currentTime - participant.drowsinessStartTime;
                    participant.totalDrowsinessTime += drowsinessDuration;
                    participant.drowsinessStartTime = null;
                    console.log(`[AI Stats] ${participant.name} 수면 종료 (지속시간: ${this.formatDuration(drowsinessDuration)})`);
                }
            }

            participant.currentStatus.isDrowsy = isNowDrowsy;
        }

        // 공통 업데이트
        participant.currentStatus.lastUpdate = currentTime;
        
        // 히스토리 기록
        participant.statusHistory.push({
            timestamp: currentTime,
            previousStatus: previousStatus,
            newStatus: { ...participant.currentStatus },
            changes: newStatus
        });

        // 이벤트 발생
        this.emit('participantStatusUpdated', {
            peerId: peerId,
            participant: this.getParticipantStats(peerId),
            changes: newStatus
        });

        // 전체 통계 업데이트 이벤트
        this.emit('overallStatsUpdated', this.getOverallStats());
    }

    /**
     * 참여자 통계 조회
     * @param {string} peerId - 참여자 ID
     * @returns {Object} 참여자 통계 데이터
     */
    getParticipantStats(peerId) {
        const participant = this.participants.get(peerId);
        if (!participant) return null;

        const currentTime = Date.now();
        const sessionDuration = currentTime - participant.joinTime;
        
        // 현재 진행 중인 자리비움/수면 시간 계산
        let currentAbsenceTime = 0;
        let currentDrowsinessTime = 0;
        
        if (!participant.currentStatus.isPresent && participant.absenceStartTime) {
            currentAbsenceTime = currentTime - participant.absenceStartTime;
        }
        
        if (participant.currentStatus.isDrowsy && participant.drowsinessStartTime) {
            currentDrowsinessTime = currentTime - participant.drowsinessStartTime;
        }

        const totalAbsenceTime = participant.totalAbsenceTime + currentAbsenceTime;
        const totalDrowsinessTime = participant.totalDrowsinessTime + currentDrowsinessTime;
        
        // 참여도 계산 (전체시간 - 자리비움시간 - 수면시간) / 전체시간 * 100
        const participationRate = Math.max(0, Math.min(100, 
            ((sessionDuration - totalAbsenceTime - totalDrowsinessTime) / sessionDuration) * 100
        ));

        return {
            ...participant,
            statistics: {
                sessionDuration: sessionDuration,
                totalAbsenceTime: totalAbsenceTime,
                totalDrowsinessTime: totalDrowsinessTime,
                participationRate: Math.round(participationRate),
                absenceMinutes: Math.round(totalAbsenceTime / 60000), // 분 단위
                drowsinessMinutes: Math.round(totalDrowsinessTime / 60000), // 분 단위
                // 상태 판정
                statusLevel: this.getStatusLevel(participationRate, participant.absenceCount, participant.drowsinessCount)
            }
        };
    }

    /**
     * 전체 통계 조회
     * @returns {Object} 전체 통계 데이터
     */
    getOverallStats() {
        const participants = Array.from(this.participants.keys()).map(peerId => 
            this.getParticipantStats(peerId)
        );

        if (participants.length === 0) {
            return {
                totalParticipants: 0,
                averageParticipationRate: 0,
                statusCounts: { excellent: 0, good: 0, warning: 0, absent: 0 }
            };
        }

        const totalParticipationRate = participants.reduce((sum, p) => 
            sum + p.statistics.participationRate, 0
        );
        
        const statusCounts = participants.reduce((counts, p) => {
            counts[p.statistics.statusLevel]++;
            return counts;
        }, { excellent: 0, good: 0, warning: 0, absent: 0 });

        return {
            totalParticipants: participants.length,
            averageParticipationRate: Math.round(totalParticipationRate / participants.length),
            statusCounts: statusCounts,
            sessionDuration: Date.now() - this.sessionStartTime
        };
    }

    /**
     * 상태 레벨 판정
     * @param {number} participationRate - 참여도 (%)
     * @param {number} absenceCount - 자리비움 횟수
     * @param {number} drowsinessCount - 수면 횟수
     * @returns {string} 'excellent', 'good', 'warning', 'absent'
     */
    getStatusLevel(participationRate, absenceCount, drowsinessCount) {
        if (participationRate < 50 || absenceCount >= 5) {
            return 'absent';  // 자리비움
        } else if (participationRate < 70 || drowsinessCount >= 3) {
            return 'warning'; // 주의
        } else if (participationRate >= 90 && absenceCount <= 1 && drowsinessCount <= 1) {
            return 'excellent'; // 우수
        } else {
            return 'good';    // 양호
        }
    }

    /**
     * 시간 포맷팅 (밀리초 -> "X분 Y초")
     */
    formatDuration(milliseconds) {
        const seconds = Math.floor(milliseconds / 1000);
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        
        if (minutes > 0) {
            return `${minutes}분 ${remainingSeconds}초`;
        } else {
            return `${remainingSeconds}초`;
        }
    }

    /**
     * 이벤트 리스너 등록
     */
    on(eventName, callback) {
        if (!this.eventListeners.has(eventName)) {
            this.eventListeners.set(eventName, []);
        }
        this.eventListeners.get(eventName).push(callback);
    }

    /**
     * 이벤트 발생
     */
    emit(eventName, data) {
        const listeners = this.eventListeners.get(eventName);
        if (listeners) {
            listeners.forEach(callback => callback(data));
        }
    }

    /**
     * 모든 참여자 목록 조회
     */
    getAllParticipants() {
        return Array.from(this.participants.keys()).map(peerId => 
            this.getParticipantStats(peerId)
        );
    }

    /**
     * 통계 초기화
     */
    reset() {
        this.participants.clear();
        this.sessionStartTime = Date.now();
        console.log('[AI Stats] 통계 데이터 초기화');
    }

    /**
     * 디버그 정보 출력
     */
    debugLog() {
        console.log('[AI Stats] 현재 통계:', {
            overall: this.getOverallStats(),
            participants: this.getAllParticipants()
        });
    }
}

// 전역 인스턴스 생성
window.aiMonitoringStats = new AIMonitoringStats();
