package no.digdir.minidnotificationserver.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.api.domain.MessageType;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.registration.passport.PassportEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.OnboardingProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.integration.idporten.IdportenEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.integration.nets.NetsClient;
import no.digdir.minidnotificationserver.integration.nets.NetsEntity;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
public class PassportOnboardingService {

    private final NetsClient netsClient;
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
       this.config  = configProvider.getAuthenticator(); // TODO: move to class instance
    }

    @Audit(auditId = AuditID.PASSPORT_ONBOARDING_START)
    public PassportEntity.Start.Response start(PassportEntity.Start.Request requestEntity) {
       

        log.debug("Starting passport onboarding of {}", requestEntity);

        if("ios".equalsIgnoreCase(requestEntity.getOs())) { // import iOS token
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
        data.put("state", requestEntity.getState());
        data.put("category", config.getPassportOnboardingCategory());

        NotificationEntity notification = NotificationEntity.builder()
                .app_identifier(config.getAppIdentifier())
                .priority(config.getPriority())
                .ttl(config.getTtl())
                .data(data)
                .build();

        NetsEntity.CibaSessionResponse cibaSessionResponse = netsClient.session();
        requestEntity.setAuth_req_id(cibaSessionResponse.getAuth_req_id());

        firebaseClient.send(notification, requestEntity.getToken(), MessageType.data);

        cache.putPassportStartEntity(requestEntity.getApns_token() != null ? requestEntity.getApns_token() : requestEntity.getToken(), requestEntity);

        return PassportEntity.Start.Response.builder()
                .auth_req_id(cibaSessionResponse.getAuth_req_id())
                .build();
    }

    @Audit(auditId = AuditID.PASSPORT_ONBOARDING_FINALIZE)
    public IdportenEntity.TokenResponse finalize(PassportEntity.Finalize.Request requestEntity) {

        String fcmOrApnsToken = requestEntity.getApns_token() != null ? requestEntity.getApns_token() : requestEntity.getToken();
        PassportEntity.Start.Request startEntity = cache.getPassportStartEntity(fcmOrApnsToken);
        cache.deletePassportStartEntity(fcmOrApnsToken);
        if(startEntity == null) {
            throw new OnboardingProblem("Invalid 'token'.");
        }

        log.debug("Continuing passport onboarding of {}", startEntity);

        // check that login_key matches
        if(!requestEntity.getLogin_key().equals(startEntity.getLogin_key())) {
            throw new OnboardingProblem("'login_key' does not match.");
        }

        // check that code_challenge/code_verifier is good
        String codeChallenge = Utils.generateCodeChallange(requestEntity.getCode_verifier());
        if(!codeChallenge.equals(startEntity.getCode_challenge())) {
            cache.deletePassportStartEntity(fcmOrApnsToken);
            throw new OnboardingProblem("'code_verifier' does not match 'code_challenge'.");
        }

        NetsEntity.TokenResponse netsTokenResponse = netsClient.token(startEntity.getAuth_req_id());

        JWT idTokenJwt;
        String personIdentifier;
        try {
            idTokenJwt = JWTParser.parse(netsTokenResponse.getId_token());
            personIdentifier = idTokenJwt.getJWTClaimsSet().getStringClaim("personal_number");
            if(StringUtils.isEmpty(personIdentifier)) {
                throw new OnboardingProblem("No person identifier in id_token");
            }
        } catch (ParseException e) {
            throw new OnboardingProblem("Error parsing id_token: " + e.getMessage());
        }

        // TODO: validate facelevel, expiry, allowed countries, other rules and more

        // TODO: create or update minid user

        // TODO: set 2fa to app
        // minIdBackendClient.setPreferredTwoFactorMethod(personIdentifier, "app");

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

        // if everything is hunk-dory, then save device in db.
        deviceService.deleteByAppId(personIdentifier, startEntity.getApp_identifier());
        deviceService.save(personIdentifier, DeviceEntity.from(startEntity));


        return tokenResponse;
    }
}
