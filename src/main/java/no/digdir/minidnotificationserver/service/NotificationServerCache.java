package no.digdir.minidnotificationserver.service;

import no.digdir.minidnotificationserver.api.attestation.NonceEntity;
import no.digdir.minidnotificationserver.api.attestation.android.AttestationEntity;
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
    final private Cache attestationNonceCache;
    final private Cache attestationCache;

    @Autowired
    public NotificationServerCache(CacheManager cacheManager, EmbeddedCacheConfiguration cacheConfiguration) {
        this.onboardingCache = cacheManager.getCache(EmbeddedCacheConfiguration.ONBOARDING_CACHE);
        this.loginAttemptCache = cacheManager.getCache(EmbeddedCacheConfiguration.LOGIN_ATTEMPT_CACHE);
        this.verificationCache = cacheManager.getCache(EmbeddedCacheConfiguration.VERIFICATION_CACHE);
        this.attestationNonceCache = cacheManager.getCache(EmbeddedCacheConfiguration.ATTESTATION_NONCE_CACHE);
        this.attestationCache = cacheManager.getCache(EmbeddedCacheConfiguration.ATTESTATION_CACHE);
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

    /*
        Attestation
     */
    public void putAttestationNonce(String fcmOrApnsToken, NonceEntity.Storage nonceEntity) {
        attestationNonceCache.put(fcmOrApnsToken, nonceEntity);
    }

    public NonceEntity.Storage getAttestationNonce(String fcmOrApnsToken){
        Cache.ValueWrapper valueWrapper = attestationNonceCache.get(fcmOrApnsToken);
        return valueWrapper != null ? (NonceEntity.Storage) valueWrapper.get() : null;
    }

    public void putAttestation(String fcmOrApnsToken, AttestationEntity.Request entity) {
        attestationCache.put(fcmOrApnsToken, entity);
    }

    public AttestationEntity.Request getAttestation(String fcmOrApnsToken){
        Cache.ValueWrapper valueWrapper = attestationCache.get(fcmOrApnsToken);
        return valueWrapper != null ? (AttestationEntity.Request) valueWrapper.get() : null;
    }
}
