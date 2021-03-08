package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import org.springframework.stereotype.Component;

import static com.google.firebase.messaging.AndroidConfig.Priority;

@Component
@RequiredArgsConstructor
public class FirebaseClient {

    // Guide: https://firebase.google.com/docs/cloud-messaging
    // API: https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/messaging/package-summary

    private final ConfigProvider configProvider;
    private final FirebaseMessaging firebaseMessaging;


    public void send(NotificationEntity notificationEntity, String token) {

        boolean highPriority = "HIGH".equalsIgnoreCase(notificationEntity.getPriority());
        long ttl = (notificationEntity.getTtl() != null) ? notificationEntity.getTtl() : 2419200L;
        String notificationImage = configProvider.getFirebase().getNotificationImageUrl();

        /* Notification */
        Notification.Builder notificationBuilder = Notification.builder();
        if(notificationEntity.getTitle() != null && !notificationEntity.getTitle().isEmpty()) {
            notificationBuilder.setTitle(notificationEntity.getTitle());
        }

        if(notificationEntity.getBody() != null && !notificationEntity.getBody().isEmpty()) {
            notificationBuilder.setBody(notificationEntity.getBody());
        }

        if (notificationImage != null && !notificationImage.isEmpty()) {
            notificationBuilder.setImage(notificationImage);
        }

        /* Android Config */
        AndroidConfig.Builder androidConfigBuilder = AndroidConfig.builder();
        androidConfigBuilder.setPriority(highPriority ? Priority.HIGH : Priority.NORMAL);
        androidConfigBuilder.setTtl(ttl * 1000);
        if (notificationEntity.getClick_action() != null && !notificationEntity.getClick_action().isEmpty()) {
            androidConfigBuilder.setNotification(AndroidNotification.builder().setClickAction(notificationEntity.getClick_action()).build());
        }

        /* iOS config */
        ApnsConfig.Builder apnsConfigBuilder = ApnsConfig.builder();
        apnsConfigBuilder.putHeader("apns-priority", highPriority ? "10" : "5"); // 10 == high, 5 == normal
        apnsConfigBuilder.putHeader("apns-expiration", Long.toString(ttl));
        if (notificationEntity.getAps_category() != null && !notificationEntity.getAps_category().isEmpty()) {
            apnsConfigBuilder.setAps(Aps.builder().setCategory(notificationEntity.getAps_category()).build());
        } else {
            apnsConfigBuilder.setAps(Aps.builder()
                    .setMutableContent(true)
                    .build());
        }

        /* Message */
        Message.Builder messageBuilder = Message.builder();
        messageBuilder
                .setNotification(notificationBuilder.build())
                .setAndroidConfig(androidConfigBuilder.build())
                .setApnsConfig(apnsConfigBuilder.build())
                .setToken(token);

        /* Data payload */
        if(notificationEntity.getData() != null) {
            messageBuilder.putAllData(notificationEntity.getData());
        }

        if(notificationEntity.getLogin_attempt_expiry() != null) {
            messageBuilder.putData("expiry", notificationEntity.getLogin_attempt_expiry().toString());
        }

        Message message = messageBuilder.build();

        try {
            String mesgId = firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
