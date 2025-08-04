package com.demo.proworks.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * 토큰을 블랙리스트에 추가
     */
    public void addToBlacklist(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "logout", expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    /**
     * 토큰의 남은 만료 시간 계산 (JwtUtil 사용)
     */
    public long getTokenRemainingTime(String token) {
        return jwtUtil.getTokenRemainingTime(token);
    }
}
