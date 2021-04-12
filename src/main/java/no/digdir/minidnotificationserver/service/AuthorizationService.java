package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.authorization.AuthorizationEntity;
import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.exceptions.LoginAttemptNotFoundProblem;
import no.digdir.minidnotificationserver.integration.minidauthentication.MinIdAuthenticationClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final NotificationServerCache cache;
    private final MinIdAuthenticationClient minIdAuthenticationClient;
    private final AuditService auditService;


    public boolean approve(String personIdentifier, AuthorizationEntity entity) {
        String loginAttemptId = getLoginAttemptId(entity);
        auditService.auditAppAuthorize(personIdentifier, entity, "approve");
        return minIdAuthenticationClient.approve(personIdentifier, loginAttemptId);
    }

    public boolean reject(String personIdentifier, AuthorizationEntity entity) {
        String loginAttemptId = getLoginAttemptId(entity);
        auditService.auditAppAuthorize(personIdentifier, entity, "reject");
        minIdAuthenticationClient.reject(personIdentifier, loginAttemptId);
        return true;
    }

    private String getLoginAttemptId(AuthorizationEntity entity) {
        String loginAttemptId = entity.getLogin_attempt_id();
        RequestAuthorizationEntity e = cache.getLoginAttempt(loginAttemptId);

        if(e != null && !e.getLogin_attempt_id().isEmpty()) {
            cache.deleteLoginAttemptId(loginAttemptId);
            return loginAttemptId;
        }
        throw new LoginAttemptNotFoundProblem(loginAttemptId);
    }

}
