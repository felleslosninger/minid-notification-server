package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.Utils;
import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestAuthorizationService {

    private final NotificationService notificationService;
    private final NotificationServerCache cache;
    private final ConfigProvider configProvider;
    private final AuditService auditService;

    public void request(RequestAuthorizationEntity entity, AdminContext adminContext) {
        NotificationEntity notificationEntity = createNotificationEntity(entity);
        cache.putLoginAttempt(entity.getLogin_attempt_id(), entity);
        auditService.auditAuthorizationRequest(entity, adminContext);
        notificationService.send(notificationEntity, adminContext);
    }
    private NotificationEntity createNotificationEntity(RequestAuthorizationEntity requestAuthorizationEntity) {
        Map<String, String> data = new HashMap<>();
        data.put("login_attempt_id", requestAuthorizationEntity.getLogin_attempt_id());
        data.put("login_attempt_counter", requestAuthorizationEntity.getLogin_attempt_counter().toString());
        data.put("login_attempt_expiry", requestAuthorizationEntity.getLogin_attempt_expiry().format(Utils.dtf));
        data.put("request_type", "LOGIN");
        data.put("service_name", requestAuthorizationEntity.getService_provider());
        data.put("requires_local_authentication", "true");
        return NotificationEntity.builder()
                .person_identifier(requestAuthorizationEntity.getPerson_identifier())
                .app_identifier(configProvider.getAuthenticator().getAppIdentifier())
                .title(requestAuthorizationEntity.getTitle())
                .body(requestAuthorizationEntity.getBody())
                .priority(configProvider.getAuthenticator().getPriority())
                .ttl(configProvider.getAuthenticator().getTtl())
                .aps_category("authCategory")
                .click_action("minid_auth_intent")
                .data(data)
                .build();
    }
}
