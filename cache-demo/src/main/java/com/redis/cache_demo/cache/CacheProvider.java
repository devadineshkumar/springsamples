package com.redis.cache_demo.cache;

import com.redis.cache_demo.dto.ProductRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheProvider {

    private final CacheManager cacheManager;

    public ProductRecord getCacheValue(String cacheName, String key) {
        return (ProductRecord) cacheManager.getCache(cacheName).get(key).get();
    }

    public void evictCache(String cacheName, String key) {
        cacheManager.getCache(cacheName).evict(key);
    }

    public void clearCache(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }

    public void putCache(String cacheName, String key, ProductRecord value) {
        cacheManager.getCache(cacheName).put(key, value);
    }

    public boolean exists(String cacheName, String key) {
        return cacheManager.getCache(cacheName).get(key) != null;
    }
}
