package com.demo.proworks.chat.service.impl;

import org.springframework.stereotype.Service;
import com.demo.proworks.chat.service.ChatService;
import com.demo.proworks.chat.vo.ChatMessageVo;
import com.demo.proworks.chat.websocket.ChatWebSocketEndpoint;

/**
 * ProWorks 표준 채팅 서비스 구현
 */
@Service("chatService")
public class ChatServiceImpl implements ChatService {
    
    @Override
    public void sendMessage(ChatMessageVo chatMessageVo) throws Exception {
        
        try {
            // WebSocket을 통한 실시간 메시지 전송
            // 실제로는 ChatWebSocketEndpoint의 브로드캐스트 기능 활용
            
            System.out.println("=== proworks 서비스 - 메시지 전송 ===");
            System.out.println("타입: " + chatMessageVo.getType());
            System.out.println("방ID: " + chatMessageVo.getRoomId());
            System.out.println("발송자: " + chatMessageVo.getSender());
            System.out.println("메시지: " + chatMessageVo.getMessage());
            

            
            // 여기서 추가 비즈니스 로직 처리 가능:
            // - 메시지 로깅 -> 이건 필요없을 듯
            // - 금지어 필터링 -> 시간 되면 구현
            // - 메시지 히스토리 저장 -> 이건 구현해야함 - 채팅 기록 저장할 경우
            // - 알림 발송 등 -> 채팅 모듈에 마크 정도 띄우면 될듯 -> 시간 되면 구현
            
        } catch (Exception e) {
            System.err.println("메시지 전송 서비스 오류: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void joinRoom(String roomId, String userId) throws Exception {
        
        try {
//            System.out.println("=== ProWorks 서비스: 채팅방 입장 ===");
//            System.out.println("방ID: " + roomId);
//            System.out.println("사용자ID: " + userId);
            
            // <여기서 추가 비즈니스 로직 처리>
            // - 방 입장 권한 확인 -> 필요없을 듯 (초대 링크로 권한을 확인하지않고 입장 가능)
            // - 사용자 정보 검증 -> 이건 필요없을듯 (초대 링크로 권한을 확인하지않고 입장 가능)
            // - 입장 로그 기록 -> 이건 구현 필요
            // - 환영 메시지 발송 -> 이건 시간 되면 구현
            
        } catch (Exception e) {
            System.err.println("채팅방 입장 서비스 오류: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void leaveRoom(String roomId, String userId) throws Exception {
        
        try {
            System.out.println("=== ProWorks 서비스: 채팅방 퇴장 ===");
            System.out.println("방ID: " + roomId);
            System.out.println("사용자ID: " + userId);
            
            // 여기서 추가 비즈니스 로직 처리:
            // - 퇴장 로그 기록
            // - 퇴장 메시지 발송
            // - 세션 정리
            
        } catch (Exception e) {
            System.err.println("채팅방 퇴장 서비스 오류: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public int getRoomUserCount(String roomId) throws Exception {
        
        try {
            // ChatWebSocketEndpoint의 정적 메서드로 사용자 수 조회
            // (실제로는 ChatWebSocketEndpoint에 getRoomUserCount 메서드 추가 필요)
            
            System.out.println("=== ProWorks 서비스: 방 사용자 수 조회 ===");
            System.out.println("방ID: " + roomId);
            
            // 임시로 0 반환 (나중에 실제 구현)
            return 0;
            
        } catch (Exception e) {
            System.err.println("사용자 수 조회 서비스 오류: " + e.getMessage());
            throw e;
        }
    }
}