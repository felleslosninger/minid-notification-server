package no.digdir.minidnotificationserver.service;


import no.digdir.minidnotificationserver.api.onboarding.OnboardingStartRequestEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OnboardingServiceCache {

    @Cacheable(value="onboarding", key="#fcmOrApnsToken.toString()")
    public OnboardingStartRequestEntity getOrSetStartEntity(String fcmOrApnsToken, OnboardingStartRequestEntity onboardingStartRequestEntity) {
        return onboardingStartRequestEntity;
    }

    @CacheEvict(value="onboarding", key="#fcmOrApnsToken.toString()" )
    public void deleteStartEntity(String fcmOrApnsToken) {
        // do nothing.
    }

}
