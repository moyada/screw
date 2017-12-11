package cn.xyk.screw.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 * Created by xueyikang on 2017/11/13.
 */
public class CacheMap<T, U> {

    private final static int DEFAULT_CACHE_TIME     = 3600;
    private final static int DEFAULT_INIT_CAPACITY  = 16;
    private final static int DEFAULT_MAX_CAPACITY   = 2048;

    private boolean refreshWithLoad;

    private final LoadingCache<T, U> cache;

    public CacheMap() {
        this(DEFAULT_CACHE_TIME, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_CAPACITY, false);
    }

    public CacheMap(final boolean refreshWithLoad) {
        this(DEFAULT_CACHE_TIME, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_CAPACITY, refreshWithLoad);
    }

    public CacheMap(final int cacheTime) {
        this(cacheTime, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_CAPACITY, false);
    }

    public CacheMap(final int cacheTime, final boolean refreshWithLoad) {
        this(cacheTime, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_CAPACITY, refreshWithLoad);
    }

    public CacheMap(final int cacheTime, final int maxSize) {
        this(cacheTime, DEFAULT_INIT_CAPACITY, maxSize, false);
    }

    public CacheMap(final int cacheTime, final int maxSize, final boolean refreshWithLoad) {
        this(cacheTime, DEFAULT_INIT_CAPACITY, maxSize, refreshWithLoad);
    }

    public CacheMap(final int cacheTime, final int initSize, final int maxSize) {
        this(cacheTime, initSize, maxSize, false);
    }

    public CacheMap(final int cacheTime, final int initSize, final int maxSize, final boolean refreshWithLoad) {
        if(cacheTime < 1) {
            throw new IllegalArgumentException("CacheMap Constructor Error: cache time can not be negative number or zero.");
        }
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(8)
                .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
                .initialCapacity(initSize)
                .maximumSize(maxSize)
                .build(new CacheLoader<T, U>() {
                    @Override
                    public U load(T t) throws Exception {
                        return null;
                    }

                    @Override
                    public ListenableFuture<U> reload(final T t, U u) {
                        return Futures.immediateFuture(u);
                    }
                });

        this.refreshWithLoad = refreshWithLoad;

        System.out.println(cache.stats());
    }

    public U get(T key) {
        U value = cache.getIfPresent(key);
        if(null == value) {
            return null;
        }
        if(refreshWithLoad) {
            cache.refresh(key);
        }
        return value;
    }

    public void put(T key, U value) {
        cache.put(key, value);
    }

    public boolean putIfNoPresent(T key, U value) {
        if(null == cache.getIfPresent(key)) {
            return false;
        }
        this.put(key, value);
        return true;
    }

    public U remove(T key) {
        U value = cache.getIfPresent(key);
        if(null == value) {
            return null;
        }
        cache.invalidate(key);
        return value;
    }

    public void refresh(T key) {
        U value = this.get(key);
        if(null == value) {
            return;
        }
        cache.refresh(key);
    }

    public boolean isEmpty() {
        return 0 == this.size();
    }

    public long size() {
        return cache.size();
    }

    public void clear() {
        cache.cleanUp();
    }

    public Map<T, U> getMap() {
        Map<T, U> cacheMap = cache.asMap();
        if(cacheMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(cacheMap);
    }

    @Override
    public String toString() {
        return "CacheMap: " + this.cache.asMap().toString();
    }
}

