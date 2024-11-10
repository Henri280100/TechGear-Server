package com.v01.techgear_server.serviceImpls;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.service.RedisConnectionService;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisConnectionServiceImpl implements RedisConnectionService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void isRedisConnected() {
        try {
            if (redisTemplate.getConnectionFactory().getConnection().ping() == null) {
                throw new RedisConnectionException("Redis is not connected.");
            }
        } catch (Exception e) {
            throw new RedisConnectionException("Redis connection error: " + e.getMessage());
        }
    }

}