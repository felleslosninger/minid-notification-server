package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.*;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final FirebaseClient firebaseClient;
    private final AuditService auditService;
    private final OnboardingServiceCache onboardingServiceCache;
    private final ConfigProvider configProvider;
    private final GoogleClient googleClient;


    public void startAuth(OnboardingStartRequestEntity entity) {
        ConfigProvider.Authenticator cfg = configProvider.getAuthenticator();

        if("ios".equalsIgnoreCase(entity.getOs())) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            auditService.auditOnboardingServiceImportApnsToken(entity, entity.getPerson_identifier(), fcmToken);
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }

        entity.setLoginKey(UUID.randomUUID().toString());
        entity.setExpiry(ZonedDateTime.now().plusSeconds(cfg.getExpiry()));

        Map<String, String> data = new HashMap<>();
        data.put("expiry", entity.getExpiry().toString());
        data.put("login_key", entity.getLoginKey());
        data.put("state", entity.getState());

        NotificationEntity notification = NotificationEntity.builder()
                .app_identifier(cfg.getAppIdentifier())
                .priority(cfg.getPriority())
                .aps_category(cfg.getApsCategory())
                .click_action(cfg.getClickAction())
                .ttl(cfg.getTtl())
                .data(data)
                .build();

        onboardingServiceCache.getOrSetStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken(), entity);
        firebaseClient.send(notification, entity.getToken());
        auditService.auditNotificationOnboardingSend(notification, entity.getPerson_identifier());
    }

    public OnboardingContinueResponseEntity continueAuth(OnboardingContinueRequestEntity entity) {
        OnboardingStartRequestEntity startEntity;
        try {
             startEntity = onboardingServiceCache.getOrSetStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken(), null);
        } catch (ClassCastException e) {
            throw new RuntimeException("Token not found.");
        }

        // check that login_key matches
        if(!entity.getLoginKey().equals(startEntity.getLoginKey())) {
            throw new RuntimeException("Login key does not match");
        }


        // TODO: verify_pw(pid, password) - delete from cache if fails
        // reply with (state, 2fa_method ("sms", "pin"), optional pin index (or reply with 401/429)
        return OnboardingContinueResponseEntity.builder()
                .two_factor_method("sms")
                .build();
    }

    public OnboardingFinalizeResponseEntity finalizeAuth(OnboardingFinalizeRequestEntity entity) {
        OnboardingStartRequestEntity startEntity;
        try {
            startEntity = onboardingServiceCache.getOrSetStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken(), null);
        } catch (ClassCastException e) {
            // TODO: delete later: onboardingServiceCache.deleteStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken());
            throw new RuntimeException("Token not found.");
        }

        // check that code_challenge/code_verifier is good
        String codeChallenge = generateCodeChallange(entity.getCode_verifier());
        if(!codeChallenge.equals(startEntity.getCode_challenge())) {
            throw new RuntimeException("code_verifier does not match code_challenge");
        }

        // check that pid matches
        if(!entity.getPerson_identifier().equals(startEntity.getPerson_identifier())) {
            throw new RuntimeException("Person identifier does not match");
        }

        // TODO: verify_sms(pid, otp) - delete from cache if fails
        // TODO: setPreferred2FAmethod("app", fcm_token, pid) - delete from cache if fails
        // TODO: fetch access_token, refresh_token from /tokenexchange (fcm_token, aud="minid-app", scope="minid:app")
        // reply with (access_token, refresh_token, state)
        // (or reply with 401/429)

        return OnboardingFinalizeResponseEntity.builder()
                .access_token("some_access_token_string")
                .refresh_token("some_refresh_token_string")
                .build();
    }


    private String generateCodeChallange(String codeVerifier)  {
        try {
            byte[] bytes = codeVerifier.getBytes("US-ASCII");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
