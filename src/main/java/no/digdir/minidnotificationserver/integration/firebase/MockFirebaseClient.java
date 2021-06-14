package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.authorization.AuthorizationEntity;
import no.digdir.minidnotificationserver.api.domain.MessageType;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@ConditionalOnExpression("'${mock.notification.enabled}'=='true'")
@Slf4j
public class MockFirebaseClient extends FirebaseClient {

    @Autowired
    private AuthorizationService authorizationService;

    public MockFirebaseClient() {
        super(null, null);
    }

    public MockFirebaseClient(ConfigProvider configProvider, FirebaseMessaging firebaseMessaging) {
        super(configProvider, firebaseMessaging);
    }

    @Override
    public void send(NotificationEntity notificationEntity, String token, MessageType messageType) {
        log.debug("Notification sent: " + notificationEntity + " token: " + token + " - messageType: " + messageType);
        AuthorizationEntity entity = AuthorizationEntity.builder()
                .login_attempt_id("asdf-1234-asdf-1234")
                .login_attempt_counter(1)
                .build();

        if (token.equals("false")) {
            authorizationService.authorize(notificationEntity.getPerson_identifier(), entity, AuthorizationService.AuthAction.REJECT);
        } else {
            authorizationService.authorize(notificationEntity.getPerson_identifier(), entity, AuthorizationService.AuthAction.APPROVE);
        }
    }
}
