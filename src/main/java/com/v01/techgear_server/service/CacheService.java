package com.v01.techgear_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_EXPIRATION = 60; // cache expiration time in seconds

    public void cacheData(String key, Object data) {
        redisTemplate.opsForValue().set(key, data, CACHE_EXPIRATION, TimeUnit.SECONDS);
    }

    public Object getCacheData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void evictCache(String key) {
        redisTemplate.delete(key);
    }
}