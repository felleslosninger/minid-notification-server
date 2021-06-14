package no.digdir.minidnotificationserver.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.api.domain.MessageType;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.OnboardingEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.OnboardingProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.integration.idporten.IdportenEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.integration.minidbackend.VerifyPwEntity;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.logging.event.EventService;
import no.digdir.minidnotificationserver.utils.Utils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final FirebaseClient firebaseClient;
    private final EventService eventService;
    private final NotificationServerCache cache;
    private final ConfigProvider configProvider;
    private final GoogleClient googleClient;
    private final DeviceService deviceService;
    private final MinIdBackendClient minIdBackendClient;
    private final IdportenService idportenService;

    @Audit(auditId = AuditID.ONBOARDING_START)
    public void startAuth(OnboardingEntity.Start.Request entity) {
        ConfigProvider.Authenticator cfg = configProvider.getAuthenticator();

        log.debug("Starting onboarding of {}", entity.getPerson_identifier());

        if("ios".equalsIgnoreCase(entity.getOs())) { // import iOS token
            String fcmToken = googleClient.importAPNsToken(entity.getToken(), entity.isApns_sandbox());
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
        firebaseClient.send(notification, entity.getToken(), MessageType.data);
    }

    @Audit(auditId = AuditID.ONBOARDING_CONTINUE)
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
        VerifyPwEntity.Response pwResponse = minIdBackendClient.verifyPassword(person_identifier, startEntity.getPassword(), "default");

        if("NORMAL".equals(pwResponse.getMinIdUserState())) { // pid & password matches, and user is not in quarantine
            OnboardingEntity.Continue.Response.ResponseBuilder builder = OnboardingEntity.Continue.Response.builder();

            OnboardingEntity.Verification.VerificationBuilder verificationBuilder = OnboardingEntity.Verification.builder();

            if(pwResponse.getPreferred2FaMethod() == null || pwResponse.getPreferred2FaMethod().isEmpty()) {
                throw new OnboardingProblem("No two-factor-method set on user.");
            }

            if("app".equals(pwResponse.getPreferred2FaMethod())) {
                throw new OnboardingProblem("Current preferred two-factor-method is 'app'.");
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

    @Audit(auditId = AuditID.ONBOARDING_FINALIZE)
    public OnboardingEntity.Finalize.Response finalizeAuth(OnboardingEntity.Finalize.Request entity) {
        String fcmOrApnsToken = entity.getApns_token() != null ? entity.getApns_token() : entity.getToken();
        OnboardingEntity.Start.Request startEntity = cache.getStartEntity(fcmOrApnsToken);
        if (startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }
        log.debug("Finalizing onboarding of {}", startEntity.getPerson_identifier());

        // check that code_challenge/code_verifier is good
        String codeChallenge = Utils.generateCodeChallange(entity.getCode_verifier());
        String personIdentifier = startEntity.getPerson_identifier();
        if(!codeChallenge.equals(startEntity.getCode_challenge())) {
            cache.deleteStartEntity(fcmOrApnsToken);
            cache.deleteVerificationEntity(personIdentifier);
            throw new OnboardingProblem("'code_verifier' does not match 'code_challenge'.");
        }

        // check that pid matches
        String claimedPersonIdentifier = entity.getPerson_identifier();
        if(!claimedPersonIdentifier.equals(personIdentifier)) {
            cache.deleteStartEntity(fcmOrApnsToken);
            cache.deleteVerificationEntity(personIdentifier);
            throw new OnboardingProblem("'personIdentifier' does not match.");
        }

        OnboardingEntity.Verification verificationEntity = cache.getVerificationEntity(personIdentifier);

        String twoFactorMethod = verificationEntity.getTwoFactorMethod();
        if("otc".equals(twoFactorMethod)) { // otc
            minIdBackendClient.verifyOtc(personIdentifier, entity.getOtc(), verificationEntity.getRequestUrn());
        } else if("pin".equals(twoFactorMethod)) { // pin code
            minIdBackendClient.verifyPin(personIdentifier, entity.getOtc(), verificationEntity.getPinCodeIndex(), verificationEntity.getRequestUrn());
        } else {
            throw new OnboardingProblem("Unknown two-factor method: '" + twoFactorMethod + "'.");
        }

        cache.deleteStartEntity(fcmOrApnsToken);
        cache.deleteVerificationEntity(personIdentifier);

        // if everything is hunk-dory, then save device in db.
        deviceService.deleteByAppId(personIdentifier, startEntity.getApp_identifier());
        deviceService.save(personIdentifier, DeviceEntity.from(startEntity));

        minIdBackendClient.setPreferredTwoFactorMethod(claimedPersonIdentifier, "app");

        IdportenEntity.TokenResponse tokenResponse = idportenService.backchannelAuthorize(personIdentifier);

        JWT jwt;
        String expiry = "";
        try {
            jwt = JWTParser.parse(tokenResponse.getAccess_token());
            Instant expInstant = jwt.getJWTClaimsSet().getExpirationTime().toInstant();
            expiry = ZonedDateTime.ofInstant(expInstant, ZoneOffset.UTC).format(Utils.dtf);
        } catch (ParseException e) {
            throw new OnboardingProblem("Error parsing access_token: " + e.getMessage());
        }

        eventService.logUserHasRegisteredDevice(personIdentifier);

        return OnboardingEntity.Finalize.Response.builder()
                .access_token(tokenResponse.getAccess_token())
                .refresh_token(tokenResponse.getRefresh_token())
                .expiry(expiry)
                .state(entity.getState())
                .build();
    }

}
