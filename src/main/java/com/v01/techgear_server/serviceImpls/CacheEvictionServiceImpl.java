package com.v01.techgear_server.serviceImpls;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.service.CacheEvictionService;

@Service
public class CacheEvictionServiceImpl implements CacheEvictionService {
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheEvictionServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void evictLRU(String cacheKey, int maxCacheSize) {
        Set<String> keys = redisTemplate.keys(cacheKey + "*");

        if (keys.size() > maxCacheSize) {
            String lruKey = findLRUKey(keys);
            if (lruKey != null) {
                redisTemplate.delete(lruKey);
            }
        }
    }

    /**
     * Given a set of keys, find the key with the smallest expiration time, i.e.,
     * the least recently used.
     * 
     * @param keys a set of keys to search
     * @return the key that was least recently used
     */
    private String findLRUKey(Set<String> keys) {
        return keys.stream()
                .map(Object::toString)
                .min(Comparator.comparingLong(key ->
                Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.MILLISECONDS))
                        .orElse(Long.MAX_VALUE)))
                .orElse(null);
    }
}
