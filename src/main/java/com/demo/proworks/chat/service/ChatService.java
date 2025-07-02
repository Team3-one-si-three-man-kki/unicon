package com.demo.proworks.chat.service;

import com.demo.proworks.chat.vo.ChatMessageVo;

/**
 * ProWorks 표준 채팅 서비스 인터페이스
 */
public interface ChatService {
    
    /**
     * 채팅 메시지 전송
     * @param chatMessageVo 채팅 메시지 정보
     * @throws Exception
     */
    void sendMessage(ChatMessageVo chatMessageVo) throws Exception;
    
    /**
     * 채팅방 입장
     * @param roomId 방 ID
     * @param userId 사용자 ID
     * @throws Exception
     */
    void joinRoom(String roomId, String userId) throws Exception;
    
    /**
     * 채팅방 퇴장
     * @param roomId 방 ID
     * @param userId 사용자 ID
     * @throws Exception
     */
    void leaveRoom(String roomId, String userId) throws Exception;
    
    /**
     * 채팅방 사용자 수 조회
     * @param roomId 방 ID
     * @return 사용자 수
     * @throws Exception
     */
    int getRoomUserCount(String roomId) throws Exception;
}