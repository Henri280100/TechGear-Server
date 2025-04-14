package com.v01.techgear_server.user.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisLoginAttemptsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_ATTEMPTS = 5; // Max allowed attempts
    private static final long LOCK_TIME_DURATION = 15; // in minutes
    private static final String KEY_PREFIX = "login_attempts:";

    public void loginFailed(String username) {
        try {
            String key = getRedisKey(username);
            Integer attempts = getAttempts(key);

            attempts += 1;

            if (attempts >= MAX_ATTEMPTS) {
                log.warn("Account locked for user: {}", username);
                redisTemplate.opsForValue().set(key, attempts, LOCK_TIME_DURATION, TimeUnit.MINUTES);
            } else {
                redisTemplate.opsForValue().set(key, attempts);
            }
        } catch (Exception e) {
            log.error("Error recording login failure for user: {}", username, e);
        }
    }

    public void loginSucceeded(String username) {
        try {
            String key = getRedisKey(username);
            redisTemplate.delete(key);
            log.info("Login succeeded for user: {}. Attempts reset.", username);
        } catch (Exception e) {
            log.error("Error resetting login attempts for user: {}", username, e);
        }
    }

    public boolean isAccountLocked(String username) {
        try {
            String key = getRedisKey(username);
            Integer attempts = getAttempts(key);
            boolean isLocked = attempts != null && attempts >= MAX_ATTEMPTS;
            if (isLocked) {
                log.warn("Account is locked for user: {}", username);
            }
            return isLocked;
        } catch (Exception e) {
            log.error("Error checking account lock status for user: {}", username, e);
            return false; // Assume not locked in case of errors
        }
    }

    public long getRemainingLockTime(String username) {
        try {
            String key = getRedisKey(username);
            Long expireTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);
            return expireTime != null ? expireTime : 0;
        } catch (Exception e) {
            log.error("Error getting remaining lock time for user: {}", username, e);
            return 0;
        }
    }

    public int getRemainingAttempts(String username) {
        try {
            String key = getRedisKey(username);
            Integer attempts = getAttempts(key);
            return MAX_ATTEMPTS - (attempts != null ? attempts : 0);
        } catch (Exception e) {
            log.error("Error getting remaining attempts for user: {}", username, e);
            return 0;
        }
    }

    private String getRedisKey(String username) {
        return KEY_PREFIX + username;
    }

    private Integer getAttempts(String key) {
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        return attempts != null ? attempts : 0;
    }
}
