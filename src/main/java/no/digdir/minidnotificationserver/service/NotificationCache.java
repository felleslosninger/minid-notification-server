package no.digdir.minidnotificationserver.service;

import no.digdir.minidnotificationserver.config.CacheConfiguration;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Service
public class NotificationCache  {

    final private Cache loginAttemptCache;


    @Autowired
    public NotificationCache(CacheManager cacheManager, CacheConfiguration cacheConfiguration) {
        this.loginAttemptCache = cacheManager.getCache(cacheConfiguration.getCacheNameLoginAttempts());

    }

    public void putLoginAttemptId(String loginAttemptId, String ssn) {
        loginAttemptCache.put(loginAttemptId, ssn);
    }

    public String getSsnForLoginAttemptId(String loginAttemptId) {
        Cache.ValueWrapper valueWrapper = loginAttemptCache.get(loginAttemptId);
        return valueWrapper != null ? (String) valueWrapper.get() : null;
    }

    public void removeLoginAttemptId(String loginAttemptId) {
        loginAttemptCache.evictIfPresent(loginAttemptId);
    }

}
