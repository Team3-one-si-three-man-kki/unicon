package com.demo.proworks.canvas;

import org.springframework.data.redis.core.RedisTemplate;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/signal.do")
public class SignalHandler {

	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	private static RedisTemplate<String, Object> redisTemplate;

	public static void setRedisTemplate(RedisTemplate<String, Object> template) {
		redisTemplate = template;
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("웹소켓 연결 성공: " + session.getId());
		sessions.add(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		if (redisTemplate == null) {
			System.err.println("RedisTemplate이 주입되지 않았습니다.");
			return;
		}
		redisTemplate.convertAndSend("canvas-updates", message);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("웹소켓 연결 종료: " + session.getId());
		sessions.remove(session);
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public static void broadcast(String message) {
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				System.err.println("메시지 전송 오류: " + e.getMessage());
			}
		});
	}
}