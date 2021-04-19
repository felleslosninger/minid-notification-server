package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.authorization.AuthorizationEntity;
import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.exceptions.LoginAttemptNotFoundProblem;
import no.digdir.minidnotificationserver.integration.minidauthentication.MinIdAuthenticationClient;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import org.springframework.stereotype.Service;

import static no.digdir.minidnotificationserver.logging.audit.AuditID.APP_AUTHORIZE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final NotificationServerCache cache;
    private final MinIdAuthenticationClient minIdAuthenticationClient;

    @Audit(auditId = APP_AUTHORIZE)
    public boolean authorize(String personIdentifier, AuthorizationEntity entity, AuthAction action) {
        String loginAttemptId = getLoginAttemptId(entity);
        return minIdAuthenticationClient.exchange(personIdentifier, loginAttemptId, action);
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

    public enum AuthAction {
        APPROVE("approve"),
        REJECT("reject");

        String action;

        AuthAction(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

}
