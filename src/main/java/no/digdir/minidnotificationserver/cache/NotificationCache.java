package no.digdir.minidnotificationserver.cache;

import org.infinispan.Cache;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class NotificationCache {

    private final InfinispanCacheManager cacheManager;
    private final String LOGIN_ATTEMPT_CACHE = "loginAttemptCache";

    public NotificationCache(InfinispanCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String putLoginAttempt(String loginAttempt, String ssn) {
        String code = UUID.randomUUID().toString();
        Cache<String, String> loginAttemptCache = cacheManager.getCache(LOGIN_ATTEMPT_CACHE);
        loginAttemptCache.put(code, ssn);
        return code;
    }

    public String getLoginAttempt(String loginAttemptId) {
        try {
            Cache<String, String> loginAttemptCache = cacheManager.getCache(LOGIN_ATTEMPT_CACHE);
            return loginAttemptCache.get(loginAttemptId);
        } catch (Exception e) {
            return null;
        }
    }

    public void removePersonResource(String loginAttemptId) {
        Cache<String, String> loginAttemptCache = cacheManager.getCache(LOGIN_ATTEMPT_CACHE);
        loginAttemptCache.remove(loginAttemptId);
    }


}
