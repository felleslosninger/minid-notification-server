package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.Utils;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.*;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.OTCFailedProblem;
import no.digdir.minidnotificationserver.exceptions.OnboardingProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.integration.minidbackend.VerifyOtcEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.VerifyPinEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.VerifyPwEntity;
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
    private final NotificationServerCache cache;
    private final ConfigProvider configProvider;
    private final GoogleClient googleClient;
    private final RegistrationService registrationService;
    private final MinIdBackendClient minIdBackendClient;


    public void startAuth(OnboardingEntity.Start.Request entity) {
        ConfigProvider.Authenticator cfg = configProvider.getAuthenticator();

        log.debug("Starting onboarding of {}", entity.getPerson_identifier());

        if("ios".equalsIgnoreCase(entity.getOs())) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            auditService.auditOnboardingServiceImportApnsToken(entity, entity.getPerson_identifier(), fcmToken);
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }

        String login_key = UUID.randomUUID().toString();
        log.debug("Setting login_key to {}", login_key);
        entity.setLogin_key(login_key);
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

        cache.putStartEntity(entity.getApns_token() != null ? entity.getApns_token() : entity.getToken(), entity);
        firebaseClient.send(notification, entity.getToken(), true);
        auditService.auditNotificationOnboardingSend(notification, entity.getPerson_identifier());
    }

    public OnboardingEntity.Continue.Response continueAuth(OnboardingEntity.Continue.Request entity) {

        String fcmOrApnsToken = entity.getApns_token() != null ? entity.getApns_token() : entity.getToken();
        OnboardingEntity.Start.Request startEntity = cache.getStartEntity(fcmOrApnsToken);
        if(startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }

        log.debug("Continuing onboarding of {}", startEntity.getPerson_identifier());

        // check that login_key matches
        if(!entity.getLogin_key().equals(startEntity.getLogin_key())) {
            throw new OnboardingProblem("'login_key' does not match.");
        }

        String person_identifier = startEntity.getPerson_identifier();
        VerifyPwEntity.Response pwResponse = minIdBackendClient.verify_password(person_identifier, startEntity.getPassword(), "default");

        if("NORMAL".equals(pwResponse.getMinIdUserState())) { // pid & password matches, and user is not in quarantine
            OnboardingEntity.Continue.Response.ResponseBuilder builder = OnboardingEntity.Continue.Response.builder();

            OnboardingEntity.Verification.VerificationBuilder verificationBuilder = OnboardingEntity.Verification.builder();

            if("app".equals(pwResponse.getPreferred2FaMethod())) {
                throw new OnboardingProblem("Preferred 2-factor-method is 'app'.");
            }

            if("pin".equals(pwResponse.getPreferred2FaMethod())) {
                Integer pinCodeIndex = pwResponse.getPinCodeIndex();
                builder.pin_index(pinCodeIndex);
                verificationBuilder.pinCodeIndex(pinCodeIndex);
            }
            verificationBuilder
                    .requestUrn(pwResponse.getRequestUrn())
                    .twoFactorMethod(pwResponse.getPreferred2FaMethod());
            cache.putVerificationEntity(person_identifier, verificationBuilder.build());

            return builder
                    .two_factor_method(pwResponse.getPreferred2FaMethod())
                    .state(startEntity.getState())
                    .build();

        } else { // should not reach here
            cache.deleteStartEntity(fcmOrApnsToken);
            throw new OnboardingProblem("Unknown state of user.");
        }


    }

    public OnboardingEntity.Finalize.Response finalizeAuth(OnboardingEntity.Finalize.Request entity) {
        String fcmOrApnsToken = entity.getApns_token() != null ? entity.getApns_token() : entity.getToken();
        OnboardingEntity.Start.Request startEntity = cache.getStartEntity(fcmOrApnsToken);
        if (startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }
        log.debug("Finalizing onboarding of {}", startEntity.getPerson_identifier());

        // check that code_challenge/code_verifier is good
        String codeChallenge = Utils.generateCodeChallange(entity.getCode_verifier());
        String person_identifier = startEntity.getPerson_identifier();
        if(!codeChallenge.equals(startEntity.getCode_challenge())) {
            cache.deleteStartEntity(fcmOrApnsToken);
            cache.deleteVerificationEntity(person_identifier);
            throw new OnboardingProblem("'code_verifier' does not match 'code_challenge'.");
        }

        // check that pid matches
        String claimed_person_identifier = entity.getPerson_identifier();
        if(!claimed_person_identifier.equals(person_identifier)) {
            cache.deleteStartEntity(fcmOrApnsToken);
            cache.deleteVerificationEntity(person_identifier);
            throw new OnboardingProblem("'person_identifier' does not match.");
        }

        OnboardingEntity.Verification verificationEntity = cache.getVerificationEntity(person_identifier);

        boolean codeVerified;
        if("otc".equals(verificationEntity.getTwoFactorMethod())) { // otc
            VerifyOtcEntity.Response otcResponse = minIdBackendClient.verify_otc(person_identifier, entity.getOtc(), verificationEntity.getRequestUrn());
            codeVerified = otcResponse.isOtcVerified();
        } else { // pin code
            VerifyPinEntity.Response pinResponse = minIdBackendClient.verify_pin(person_identifier, entity.getOtc(), verificationEntity.getPinCodeIndex(), verificationEntity.getRequestUrn());
            codeVerified = pinResponse.isPinCodeVerified();
        }

        cache.deleteStartEntity(fcmOrApnsToken);
        cache.deleteVerificationEntity(person_identifier);

        if(!codeVerified) {
            throw new OTCFailedProblem("The one-time-code from SMS or pin code letter was not accepted.");
        }

        // if everything is hunk-dory, then save device in db.
        registrationService.upsertDevice(person_identifier, RegistrationEntity.from(startEntity));

        // TODO: fetch access_token, refresh_token from /tokenexchange (fcm_token, aud="minid-app", scope="minid:app")

        return OnboardingEntity.Finalize.Response.builder()
                .access_token("some_access_token_string")
                .refresh_token("some_refresh_token_string")
                .expiry(ZonedDateTime.now().plus(Duration.ofHours(1L)).format(Utils.dtf))
                .state(entity.getState())
                .build();
    }

}
