package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.api.domain.MessageType;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.pin.PinOnboardingEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.OnboardingProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.integration.idporten.IdportenEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIDUserEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.utils.Utils;
import no.idporten.domain.user.Pincodes;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PinOnboardingService {
    private final GoogleClient googleClient;
    private final ConfigProvider configProvider;
    private final NotificationServerCache cache;
    private final FirebaseClient firebaseClient;
    private final MinIdBackendClient minIdBackendClient;
    private final IdportenService idportenService;
    private final DeviceService deviceService;

    private ConfigProvider.Authenticator config;

    @PostConstruct
    public void init() {
        this.config = configProvider.getAuthenticator();
    }

    @Audit(auditId = AuditID.PIN_ONBOARDING_START)
    public PinOnboardingEntity.Start.Response startOnboarding(PinOnboardingEntity.Start.Request requestEntity) {
        log.debug("Starting pin onboarding of {}", requestEntity);

        if ("ios".equalsIgnoreCase(requestEntity.getOs())) { // import iOS token
            String fcmToken = googleClient.importAPNsToken(requestEntity.getToken(), requestEntity.isApns_sandbox());
            requestEntity.setApns_token(requestEntity.getToken());
            requestEntity.setToken(fcmToken);
        }

        String login_key = UUID.randomUUID().toString();
        log.debug("Setting login_key to {}", login_key);
        requestEntity.setLogin_key(login_key);
        requestEntity.setExpiry(ZonedDateTime.now().plusSeconds(config.getExpiry()));

        Map<String, String> data = new HashMap<>();
        data.put("expiry", requestEntity.getExpiry().format(Utils.dtf));
        data.put("login_key", requestEntity.getLogin_key());
        data.put("category", config.getPinOnboardingCategory());

        NotificationEntity notification = NotificationEntity.builder()
                .app_identifier(config.getAppIdentifier())
                .priority(config.getPriority())
                .ttl(config.getTtl())
                .data(data)
                .build();

        MinIDUserEntity.Response userEntity = minIdBackendClient.getUser(requestEntity.getPerson_identifier());
        Integer pinCodeIndex = userEntity.getPinCodeIndex();
        Integer pinCodeIndex2 = userEntity.getPinCodeIndex2();

        requestEntity.setMinIDUser(userEntity);

        cache.putPinStartEntity(requestEntity.getApns_token() != null ? requestEntity.getApns_token() : requestEntity.getToken(), requestEntity);

        firebaseClient.send(notification, requestEntity.getToken(), MessageType.data);

        return PinOnboardingEntity.Start.Response.builder()
                .pin_index(pinCodeIndex)
                .pin_index_2(pinCodeIndex2)
                .build();

    }

    @Audit(auditId = AuditID.PIN_ONBOARDING_CONTINUE)
    public void continueOnboarding(PinOnboardingEntity.Continue.Request requestEntity) {
        String fcmOrApnsToken = requestEntity.getApns_token() != null ? requestEntity.getApns_token() : requestEntity.getToken();
        PinOnboardingEntity.Start.Request startEntity = cache.getPinStartEntity(fcmOrApnsToken);

        // update startEntity with pin codes for verification later
        String pin = requestEntity.getPin();
        String pin2 = requestEntity.getPin_2();
        startEntity.setPin(pin);
        startEntity.setPin2(pin2);
        cache.putPinStartEntity(fcmOrApnsToken, startEntity);

        // check that login_key matches
        if (!requestEntity.getLogin_key().equals(startEntity.getLogin_key())) {
            cache.deletePinStartEntity(fcmOrApnsToken);
            throw new OnboardingProblem("'login_key' does not match.");
        }

        // validate pin codes
        MinIDUserEntity.Response minIDUser = startEntity.getMinIDUser();
        Pincodes pincodes = minIDUser.getPincodes();
        if(!pincodes.isEqual(pin, minIDUser.getPinCodeIndex()) || !pincodes.isEqual(pin2, minIDUser.getPinCodeIndex2())) {
            cache.deletePinStartEntity(fcmOrApnsToken);
            throw new OnboardingProblem("Incorrect pin.");
        }
    }

    @Audit(auditId = AuditID.PIN_ONBOARDING_FINALIZE)
    public IdportenEntity.TokenResponse finalizeOnboarding(PinOnboardingEntity.Finalize.Request requestEntity) {
        String fcmOrApnsToken = requestEntity.getApns_token() != null ? requestEntity.getApns_token() : requestEntity.getToken();
        PinOnboardingEntity.Start.Request startEntity = cache.getPinStartEntity(fcmOrApnsToken);
        cache.deletePinStartEntity(fcmOrApnsToken); // not needed any more
        if (startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }

        log.debug("Continuing pin onboarding of {}", startEntity);


        // check that code_challenge/code_verifier is good
        String codeChallenge = Utils.generateCodeChallange(requestEntity.getCode_verifier());
        if (!codeChallenge.equals(startEntity.getCode_challenge())) {
            throw new OnboardingProblem("'code_verifier' does not match 'code_challenge'.");
        }

        String personIdentifier = startEntity.getPerson_identifier();
        minIdBackendClient.setPreferredTwoFactorMethod(startEntity.getPerson_identifier(), "app");
        minIdBackendClient.setPassword(personIdentifier, requestEntity.getPassword());
        String pin = startEntity.getPin();
        String pin2 = startEntity.getPin2();
        Integer pinIndex = startEntity.getMinIDUser().getPinCodeIndex();
        Integer pinIndex2 = startEntity.getMinIDUser().getPinCodeIndex2();
        minIdBackendClient.verifyPin(personIdentifier, pin, pinIndex, pin2, pinIndex2);

        IdportenEntity.TokenResponse tokenResponse = idportenService.backchannelAuthorize(personIdentifier);

        // if everything is hunk-dory, then save device in db.
        deviceService.deleteByAppId(personIdentifier, startEntity.getApp_identifier());
        deviceService.save(personIdentifier, DeviceEntity.from(startEntity));

        return tokenResponse;
    }
}
