package com.demo.proworks.chat.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 독립 톰캣 + Java EE 표준 WebSocket -> 톰캣 내장 WebSocket 엔진 활용
 */
@ServerEndpoint("/chat/{roomId}")
public class ChatWebSocketEndpoint {
    
    // 룸별로 세션 관리
    private static Map<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        // 룸별 세션 저장
        roomSessions.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>()))
                   .add(session);
        
        // 세션에 룸ID 저장
        session.getUserProperties().put("roomId", roomId);
        
        System.out.println("room - " + roomId + "] 새 클라이언트 연결: " + session.getId());
        System.out.println("room - " + roomId + "] 현재 인원: " + roomSessions.get(roomId).size());
    }
    
    @OnClose
    public void onClose(Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        if (roomId != null && roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(session);
            System.out.println("room - " + roomId + "] 클라이언트 연결 해제: " + session.getId());
            System.out.println("room - " + roomId + "] 현재 인원: " + roomSessions.get(roomId).size());
            
            // 빈 룸 정리
            if (roomSessions.get(roomId).isEmpty()) {
                roomSessions.remove(roomId);
                System.out.println("룸 [" + roomId + "] 삭제됨");
            }
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        System.out.println("룸 [" + roomId + "] 메시지 수신: " + message);
        
        // 같은 룸의 다른 사용자들에게만 브로드캐스트
        Set<Session> roomUsers = roomSessions.get(roomId);
        if (roomUsers != null) {
            for (Session s : roomUsers) {
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    try {
                        s.getBasicRemote().sendText(message);
                        System.out.println("룸 [" + roomId + "] 메시지 전송됨: " + s.getId());
                    } catch (IOException e) {
                        System.err.println("메시지 전송 실패: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        String roomId = (String) session.getUserProperties().get("roomId");
        System.err.println("룸 [" + roomId + "] WebSocket 오류: " + throwable.getMessage());
        
        if (roomId != null && roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(session);
        }
    }
}