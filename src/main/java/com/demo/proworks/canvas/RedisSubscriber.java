package com.demo.proworks.canvas;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber implements MessageListener {

	public RedisSubscriber() {
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String payload = new String(message.getBody());
		SignalHandler.broadcast(payload);
	}
}