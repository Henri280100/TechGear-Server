package com.v01.techgear_server.user.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterService {
    private final StringRedisTemplate redisTemplate;
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
        try {
            String key = "emailSent:" + email;
            String lastSentTimeStr = redisTemplate.opsForValue().get(key);
            if (lastSentTimeStr != null) {
                LocalDateTime lastSentTime = LocalDateTime.parse(lastSentTimeStr, formatter);
                LocalDateTime now = LocalDateTime.now();
                return lastSentTime.plusNanos(timeWindowMillis * 1_000_000).isAfter(now);
            }

            return false;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed while checking email sent status", e);
            return false; // Assume email hasn't been sent if Redis is down
        } catch (Exception e) {
            log.error("Unexpected error while checking email sent status", e);
            return false;

        }
    }

    // Record the time when the email is sent
    public void recordEmailSent(String email) {
        try {
            String key = "emailSent:" + email;
            redisTemplate.opsForValue().set(key, LocalDateTime.now().format(formatter));

        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed while recording email sent", e);
        } catch (Exception e) {

            log.error("Unexpected error while recording email sent", e);

        }
    }

    public boolean isRateLimited(String key) {
        try {
            String rateLimitKey = "rate_limit:" + key;
            RedisScript<Boolean> script = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, Boolean.class);

            Boolean isAllowed = redisTemplate.execute(
                    script,
                    Collections.singletonList(rateLimitKey),
                    String.valueOf(MAX_REQUESTS),
                    String.valueOf(TIME_WINDOW_MILLIS));

            return !isAllowed;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed while checking rate limit", e);
            return true; // Assume rate limited if Redis is down
        } catch (Exception e) {
            log.error("Unexpected error while checking rate limit", e);
            return true;
        }
    }

    /**
     * Tries to consume a token for a specific user within a rate limit window.
     * 
     * @param key       The Redis key (e.g., userId or IP address)
     * @param rateLimit The maximum allowed requests within the time window
     * @param duration  The time window duration in seconds
     * @return true if the request is allowed, false if rate limit exceeded
     */
    public boolean tryConsume(String key, int rateLimit, Duration duration) {
        String redisKey = "rate_limiter:" + key;

        // Increment the user's counter and set expiration if the key does not exist
        Long currentCount = redisTemplate.opsForValue().increment(redisKey, 1);
        if (currentCount == null) {
            // If the increment failed, set the initial count to 1
            currentCount = 1L;
            redisTemplate.opsForValue().set(redisKey, String.valueOf(currentCount), duration.toSeconds(), TimeUnit.SECONDS);
        }
        if (currentCount == 1) {
            // Key did not exist before, set expiration time
            redisTemplate.expire(redisKey, duration.toSeconds(), TimeUnit.SECONDS);
        }

        // Check if the current count exceeds the rate limit
        return currentCount <= rateLimit;
    }

    public CompletableFuture<Void> recordEmailSentAsync(String email) {
        return CompletableFuture.runAsync(() -> recordEmailSent(email));
    }
}
