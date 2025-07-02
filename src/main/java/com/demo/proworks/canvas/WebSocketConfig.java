package com.demo.proworks.canvas; 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import javax.annotation.PostConstruct;

@Configuration
public class WebSocketConfig {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebSocketConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@PostConstruct
	public void injectRedisTemplate() {
		SignalHandler.setRedisTemplate(redisTemplate);
	}
}