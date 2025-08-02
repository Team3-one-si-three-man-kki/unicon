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

	private static final String CANVAS_CHANNEL = "canvas-updates";


	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
//		return new LettuceConnectionFactory("cluster1.zlp9yi.ng.0001.apn2.cache.amazonaws.com", 6380);
		 return new LettuceConnectionFactory("localhost", 6380);
	}

	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new StringRedisSerializer()); 
		return template;
	}

	/**
	 * Redis 메시지를 구독(subscribe)하는 리스너들을 담을 컨테이너 설정
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter listenerAdapter) { 
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