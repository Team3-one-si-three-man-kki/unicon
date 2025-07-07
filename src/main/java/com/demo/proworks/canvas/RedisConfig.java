package com.demo.proworks.canvas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory; // 추가
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	// Redis 채널 이름 정의
	private static final String CANVAS_CHANNEL = "canvas-updates";

	/**
	 * Redis 연결을 위한 ConnectionFactory를 Bean으로 등록 
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory("localhost", 6379);
	}

	/**
	 * Redis에 메시지를 발행(publish)하기 위한 RedisTemplate 설정 (이제 redisConnectionFactory()
	 * 메서드가 만든 Bean을 자동으로 주입받음)
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) { // 매개변수 이름 변경
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	/**
	 * Redis 메시지를 구독(subscribe)하는 리스너들을 담을 컨테이너 설정
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter listenerAdapter) { // 매개변수 이름 변경
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(listenerAdapter, new ChannelTopic(CANVAS_CHANNEL));
		return container;
	}

	/**
	 * 실제 메시지를 수신하고 처리할 RedisSubscriber를 리스너로 등록
	 */
	@Bean
	public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "onMessage");
	}
}