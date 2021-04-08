package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.service.AuthenticationService;
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
    private AuthenticationService authenticationService;

    public MockFirebaseClient() {
        super(null, null);
    }

    public MockFirebaseClient(ConfigProvider configProvider, FirebaseMessaging firebaseMessaging) {
        super(configProvider, firebaseMessaging);
    }

    @Override
    public void send(NotificationEntity notificationEntity, String token) {
        log.debug("Notification sent " + notificationEntity + " token: " + token);
        if (token.equals("false")) {
            authenticationService.sendRejection(notificationEntity.getPerson_identifier());
        } else {
            authenticationService.sendApproval(notificationEntity.getPerson_identifier());
        }
    }

    @Override
    public void send(NotificationEntity notificationEntity, String token, boolean background) {
        log.debug("Notification sent: " + notificationEntity + " token: " + token + " - background: " + background);
        if (token.equals("false")) {
            authenticationService.sendRejection(notificationEntity.getPerson_identifier());
        } else {
            authenticationService.sendApproval(notificationEntity.getPerson_identifier());
        }
    }
}
