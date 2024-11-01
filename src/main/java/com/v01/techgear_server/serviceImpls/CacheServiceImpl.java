package com.v01.techgear_server.serviceImpls;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.service.CacheService;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String key, Object value) {
        put(key, value, 30, TimeUnit.MINUTES);
    }

    /**
     * Stores the given value in the cache with the specified key and expiration
     * time.
     *
     * @param key        The unique identifier for the cached value.
     * @param value      The value to be cached.
     * @param expireTime The duration after which the cached value should expire.
     * @param timeUnit   The time unit for the expiration time.
     */
    @Override
    public void put(String key, Object value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * Retrieves the cached value associated with the specified key.
     *
     * @param key The unique identifier for the cached value.
     * @return The cached value, or null if no value is associated with the key.
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Deletes the cached value associated with the specified key.
     *
     * @param key The unique identifier for the cached value to be deleted.
     */
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Sets an expiration time for the cached value associated with the specified
     * key.
     * If the key is already expired, it will be removed from the cache.
     *
     * @param key        The unique identifier for the cached value.
     * @param expireTime The duration after which the cached value should expire.
     * @param timeUnit   The time unit for the expiration time.
     */
    @Override
    public void expire(String key, long expireTime, TimeUnit timeUnit) {
        redisTemplate.expire(key, expireTime, timeUnit);
    }

    @Override
    public void hashPut(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public void hashDelete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Set<Object> hashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    @Override
    public Long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }
}
