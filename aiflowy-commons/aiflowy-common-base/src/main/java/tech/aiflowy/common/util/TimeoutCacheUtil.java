package tech.aiflowy.common.util;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

public class TimeoutCacheUtil {
    private static CacheManager timeoutCacheManager;

    private static CacheManager timeoutCacheManager() {
        if (timeoutCacheManager != null) {
            return timeoutCacheManager;
        }
        synchronized (TimeoutCacheUtil.class) {
            if (timeoutCacheManager == null) {
                timeoutCacheManager = SpringContextUtil.getBean("timeoutCacheManager");
            }
            if (timeoutCacheManager == null) {
                timeoutCacheManager = new CaffeineCacheManager();
            }
        }
        return timeoutCacheManager;
    }

    public static <T> T get(String cacheName, Object key) {
        Cache cache = timeoutCacheManager().getCache(cacheName);
        if (cache == null) return null;
        Cache.ValueWrapper wrapper = cache.get(key);
        //noinspection unchecked
        return wrapper != null ? (T) wrapper.get() : null;
    }

    public static void put(String cacheName, Object key, Object value) {
        Cache cache = timeoutCacheManager().getCache(cacheName);
        if (cache != null) cache.put(key, value);
    }

    public static void remove(String cacheName, Object key) {
        Cache cache = timeoutCacheManager().getCache(cacheName);
        if (cache != null) cache.evict(key);
    }

    public static void clear(String cacheName) {
        Cache cache = timeoutCacheManager().getCache(cacheName);
        if (cache != null) cache.clear();
    }
}
