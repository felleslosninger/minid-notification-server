package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.Utils;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.*;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.OnboardingProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final FirebaseClient firebaseClient;
    private final AuditService auditService;
    private final OnboardingServiceCache onboardingServiceCache;
    private final ConfigProvider configProvider;
    private final GoogleClient googleClient;
    private final RegistrationService registrationService;


    public void startAuth(OnboardingStartRequestEntity entity) {
        ConfigProvider.Authenticator cfg = configProvider.getAuthenticator();

        if("ios".equalsIgnoreCase(entity.getOs())) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            auditService.auditOnboardingServiceImportApnsToken(entity, entity.getPerson_identifier(), fcmToken);
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }

        entity.setLogin_key(UUID.randomUUID().toString());
        entity.setExpiry(ZonedDateTime.now().plusSeconds(cfg.getExpiry()));

        Map<String, String> data = new HashMap<>();
        data.put("expiry", entity.getExpiry().format(Utils.dtf));
        data.put("login_key", entity.getLogin_key());
        data.put("state", entity.getState());
        data.put("category", cfg.getOnboardingCategory());

        NotificationEntity notification = NotificationEntity.builder()
                .app_identifier(cfg.getAppIdentifier())
                .priority(cfg.getPriority())
                .ttl(cfg.getTtl())
                .data(data)
                .build();

        onboardingServiceCache.putStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken(), entity);
        firebaseClient.send(notification, entity.getToken(), true);
        auditService.auditNotificationOnboardingSend(notification, entity.getPerson_identifier());
    }

    public OnboardingContinueResponseEntity continueAuth(OnboardingContinueRequestEntity entity) {

        OnboardingStartRequestEntity startEntity = onboardingServiceCache.getStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken());

        if(startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }

        // check that login_key matches
        if(!entity.getLogin_key().equals(startEntity.getLogin_key())) {
            throw new OnboardingProblem("'login_key' does not match.");
        }


        // TODO: verify_pw(pid, password) - delete from cache if fails
        // reply with (state, 2fa_method ("sms", "pin"), optional pin index (or reply with 401/429)
        return OnboardingContinueResponseEntity.builder()
                .two_factor_method("sms")
                .state(startEntity.getState())
                .build();
    }

    public OnboardingFinalizeResponseEntity finalizeAuth(OnboardingFinalizeRequestEntity entity) {
        OnboardingStartRequestEntity startEntity = onboardingServiceCache.getStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken());
        if (startEntity == null) {
            // TODO: delete later: onboardingServiceCache.deleteStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken());
            throw new OnboardingProblem("Invalid 'token'.");
        }

        // check that code_challenge/code_verifier is good
        String codeChallenge = Utils.generateCodeChallange(entity.getCode_verifier());
        if(!codeChallenge.equals(startEntity.getCode_challenge())) {
            throw new OnboardingProblem("'code_verifier' does not match 'code_challenge'.");
        }

        // check that pid matches
        if(!entity.getPerson_identifier().equals(startEntity.getPerson_identifier())) {
            throw new OnboardingProblem("'person_identifier' does not match.");
        }

        // TODO: verify_sms(pid, otp) - delete from cache if fails
        // TODO: setPreferred2FAmethod("app", fcm_token, pid) - delete from cache if fails
        // TODO: fetch access_token, refresh_token from /tokenexchange (fcm_token, aud="minid-app", scope="minid:app")
        // reply with (access_token, refresh_token, state)
        // (or reply with 401/429)

        // if everything is hunk-dory, then save device in db.
        registrationService.upsertDevice(startEntity.getPerson_identifier(), RegistrationEntity.from(startEntity));

        return OnboardingFinalizeResponseEntity.builder()
                .access_token("some_access_token_string")
                .refresh_token("some_refresh_token_string")
                .expiry(ZonedDateTime.now().plus(Duration.ofHours(1L)).toString())
                .state(entity.getState())
                .build();
    }

}
