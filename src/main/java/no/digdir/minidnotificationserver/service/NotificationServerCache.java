package no.digdir.minidnotificationserver.service;

import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.api.onboarding.OnboardingEntity;
import no.digdir.minidnotificationserver.config.EmbeddedCacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class NotificationServerCache {

    final private Cache onboardingCache;
    final private Cache loginAttemptCache;
    final private Cache verificationCache;

    @Autowired
    public NotificationServerCache(CacheManager cacheManager, EmbeddedCacheConfiguration cacheConfiguration) {
        this.onboardingCache = cacheManager.getCache(EmbeddedCacheConfiguration.ONBOARDING_CACHE);
        this.loginAttemptCache = cacheManager.getCache(EmbeddedCacheConfiguration.LOGIN_ATTEMPT_CACHE);
        this.verificationCache = cacheManager.getCache(EmbeddedCacheConfiguration.VERIFICATION_CACHE);
    }

    /*
        Onboarding Start
     */
    public void putStartEntity(String fcmOrApnsToken, OnboardingEntity.Start.Request onboardingStartRequestEntity) {
        onboardingCache.put(fcmOrApnsToken, onboardingStartRequestEntity);
    }

    public OnboardingEntity.Start.Request getStartEntity(String fcmOrApnsToken) {
        Cache.ValueWrapper valueWrapper = onboardingCache.get(fcmOrApnsToken);
        return valueWrapper != null ? (OnboardingEntity.Start.Request) valueWrapper.get() : null;
    }

    public void deleteStartEntity(String fcmOrApnsToken) {
        onboardingCache.evictIfPresent(fcmOrApnsToken);
    }


    /*
        Login attempts
    */
    public void putLoginAttempt(String key, RequestAuthorizationEntity entity) {
        loginAttemptCache.put(key, entity);
    }

    public RequestAuthorizationEntity getLoginAttempt(String key) {
        Cache.ValueWrapper valueWrapper = loginAttemptCache.get(key);
        return valueWrapper != null ? (RequestAuthorizationEntity) valueWrapper.get() : null;
    }

    public void deleteLoginAttemptId(String loginAttemptId) {
        loginAttemptCache.evictIfPresent(loginAttemptId);
    }

    /*
        Verification
     */
    public void putVerificationEntity(String pid, OnboardingEntity.Verification entity) {
        verificationCache.put(pid, entity);
    }

    public OnboardingEntity.Verification getVerificationEntity(String pid) {
        Cache.ValueWrapper valueWrapper = verificationCache.get(pid);
        return valueWrapper != null ? (OnboardingEntity.Verification) valueWrapper.get() : null;
    }

    public void deleteVerificationEntity(String pid) {
        verificationCache.evictIfPresent(pid);
    }
}
