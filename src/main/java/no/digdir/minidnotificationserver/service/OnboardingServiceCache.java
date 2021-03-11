package no.digdir.minidnotificationserver.service;


import no.digdir.minidnotificationserver.api.onboarding.OnboardingStartRequestEntity;
import no.digdir.minidnotificationserver.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class OnboardingServiceCache {

    final private Cache onboardingCache;


    @Autowired
    public OnboardingServiceCache(CacheManager cacheManager, CacheConfiguration cacheConfiguration) {
        this.onboardingCache = cacheManager.getCache(cacheConfiguration.getCacheNameLoginAttempts());

    }

    public void putStartEntity(String fcmOrApnsToken, OnboardingStartRequestEntity onboardingStartRequestEntity) {
        onboardingCache.put(fcmOrApnsToken, onboardingStartRequestEntity);
    }

    public OnboardingStartRequestEntity getStartEntity(String fcmOrApnsToken) {
        Cache.ValueWrapper valueWrapper = onboardingCache.get(fcmOrApnsToken);
        return valueWrapper != null ? (OnboardingStartRequestEntity) valueWrapper.get() : null;
    }

    public void deleteStartEntity(String fcmOrApnsToken) {
        onboardingCache.evictIfPresent(fcmOrApnsToken);
    }
}
