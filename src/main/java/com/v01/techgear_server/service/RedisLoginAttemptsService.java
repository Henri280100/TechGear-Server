package com.v01.techgear_server.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisLoginAttemptsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_ATTEMPTS = 5; // Max allowed attempts
    private static final long LOCK_TIME_DURATION = 15;

    public void loginFailed(String username) {
        String key = getRedisKey(username);
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);

        if (attempts == null) {
            attempts = 0;
        }

        attempts += 1;

        if (attempts >= MAX_ATTEMPTS) {
            // Lock the account by setting an expiry in Redis
            redisTemplate.opsForValue().set(key, attempts, LOCK_TIME_DURATION, TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().set(key, attempts);
        }
    }

    public void loginSucceeded(String username) {
        String key = getRedisKey(username);
        redisTemplate.delete(key); // Reset the login attempts on success
    }

    public boolean isAccountLocked(String username) {
        String key = getRedisKey(username);
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        return attempts != null && attempts >= MAX_ATTEMPTS;
    }

    private String getRedisKey(String username) {
        return "login_attempts:" + username;
    }
}
