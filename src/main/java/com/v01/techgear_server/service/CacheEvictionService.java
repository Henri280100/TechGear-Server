package com.v01.techgear_server.service;

public interface CacheEvictionService {
    void evictLRU(String cacheKey, int maxCacheSize);
}
