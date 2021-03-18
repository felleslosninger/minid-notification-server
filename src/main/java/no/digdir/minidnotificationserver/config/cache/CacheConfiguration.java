package no.digdir.minidnotificationserver.config.cache;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CacheConfiguration {
    @Value("${cache.internal-id:'default'}")
    private String cachePrefix;
    private final static String LOGIN_ATTEMPT_CACHE = "loginAttemptCache";
    private final static String ONBOARDING_CACHE = "onboardingCache";

    public String getCacheNameLoginAttempts() {
        return cachePrefix + "-" + LOGIN_ATTEMPT_CACHE;
    }

    public String getCacheNameOnboarding() {
        return cachePrefix + "-" + ONBOARDING_CACHE;
    }
}
