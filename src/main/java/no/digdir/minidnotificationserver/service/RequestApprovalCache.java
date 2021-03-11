package no.digdir.minidnotificationserver.service;

import no.digdir.minidnotificationserver.api.internal.aproval.RequestApprovalEntity;
import no.digdir.minidnotificationserver.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Service
public class RequestApprovalCache {

    final private Cache loginAttemptCache;


    @Autowired
    public RequestApprovalCache(CacheManager cacheManager, CacheConfiguration cacheConfiguration) {
        this.loginAttemptCache = cacheManager.getCache(cacheConfiguration.getCacheNameLoginAttempts());

    }

    public void putLoginAttempt(String key, RequestApprovalEntity notificationEntity) {
        loginAttemptCache.put(key, notificationEntity);
    }

    public RequestApprovalEntity getApprovalNotificationForLoginAttempt(String key) {
        Cache.ValueWrapper valueWrapper = loginAttemptCache.get(key);
        return valueWrapper != null ? (RequestApprovalEntity) valueWrapper.get() : null;
    }

    public void removeLoginAttemptId(String loginAttemptId) {
        loginAttemptCache.evictIfPresent(loginAttemptId);
    }

}
