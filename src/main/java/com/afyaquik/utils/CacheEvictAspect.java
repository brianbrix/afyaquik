package com.afyaquik.utils;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheEvictAspect {

    private final CacheManager cacheManager;

    public CacheEvictAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Pointcut for any method with save/update/delete in the name
    @After("execution(* com.afyaquik..*Service.*(..)) && " +
            "(execution(* *save*(..)) || execution(* *update*(..)) || execution(* *create*(..)) || execution(* *add*(..)) || execution(* *delete*(..)))")
    public void clearSearchCacheAfterWrite() {
        // Invalidate all entries in the searchResults cache
        if (cacheManager.getCache("searchResults") != null) {
            cacheManager.getCache("searchResults").clear();
        }
    }
}
