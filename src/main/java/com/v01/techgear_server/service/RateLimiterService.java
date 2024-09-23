package com.v01.techgear_server.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW_MILLIS = 60000;
    
    private static final String RATE_LIMIT_SCRIPT = "local key = KEYS[1] " +
            "local maxRequests = tonumber(ARGV[1]) " +
            "local timeWindowMillis = tonumber(ARGV[2]) " +
            "local currentCount = redis.call('INCR', key) " +
            "if currentCount == 1 then " +
            "   redis.call('PEXPIRE', key, timeWindowMillis) " +
            "end " +
            "return currentCount <= maxRequests";

    // Check if email has already been sent within a certain time window
    public boolean hasEmailBeenSent(String email, long timeWindowMillis) {
        String key = "emailSent:" + email;
        String lastSentTimeStr = redisTemplate.opsForValue().get(key);

        if (lastSentTimeStr != null) {
            LocalDateTime lastSentTime = LocalDateTime.parse(lastSentTimeStr, formatter);
            LocalDateTime now = LocalDateTime.now();

            return lastSentTime.plusNanos(timeWindowMillis * 1_000_000).isAfter(now);
        }

        return false;
    }

    // Record the time when the email is sent
    public void recordEmailSent(String email) {
        String key = "emailSent:" + email;
        redisTemplate.opsForValue().set(key, LocalDateTime.now().format(formatter));
    }

    public boolean isRateLimited(String key) {
        String rateLimitKey = "rate_limit:" + key;
        RedisScript<Boolean> script = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, Boolean.class);

        Boolean isAllowed = redisTemplate.execute(
                script,
                Collections.singletonList(rateLimitKey),
                String.valueOf(MAX_REQUESTS),
                String.valueOf(TIME_WINDOW_MILLIS));

        return isAllowed == null || !isAllowed;
    }

     /**
     * Tries to consume a token for a specific user within a rate limit window.
     * @param key The Redis key (e.g., userId or IP address)
     * @param rateLimit The maximum allowed requests within the time window
     * @param duration The time window duration in seconds
     * @return true if the request is allowed, false if rate limit exceeded
     */
    public boolean tryConsume(String key, int rateLimit, Duration duration) {
        String redisKey = "rate_limiter:" + key;

        // Increment the user's counter and set expiration if the key does not exist
        Long currentCount = redisTemplate.opsForValue().increment(redisKey, 1);
        if (currentCount == 1) {
            // Key did not exist before, set expiration time
            redisTemplate.expire(redisKey, duration.toSeconds(), TimeUnit.SECONDS);
        }

        // Check if the current count exceeds the rate limit
        return currentCount <= rateLimit;
    }
}
