package com.v01.techgear_server.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CacheService {
    void put(String key, Object value);

    void put(String key, Object value, long expireTime, TimeUnit timeUnit);

    Object get(String key);

    void delete(String key);

    void expire(String key, long expireTime, TimeUnit timeUnit);

    // New hash operations

    void hashPut(String key, String hashKey, Object value);

    Object hashGet(String key, String hashKey);

    void hashDelete(String key, String hashKey);

    Set<Object> hashKeys(String key);

    Long hashSize(String key);
}