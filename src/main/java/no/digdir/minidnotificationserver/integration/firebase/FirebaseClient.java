package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.IncomingHttpResponse;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.domain.MessageType;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.firebase.messaging.AndroidConfig.Priority;
import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseClient {

    // Guide: https://firebase.google.com/docs/cloud-messaging
    // API: https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/messaging/package-summary
    // APNs: https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/generating_a_remote_notification

    private final ConfigProvider configProvider;
    private final FirebaseMessaging firebaseMessaging;

    @Audit(auditId = AuditID.NOTIFICATION_SEND)
    public void send(NotificationEntity notificationEntity, String token, MessageType messageType) {

        log.debug("firebase client sending notification to token: " + token);

        boolean highPriority = "HIGH".equalsIgnoreCase(notificationEntity.getPriority());
        long ttl = (notificationEntity.getTtl() != null) ? notificationEntity.getTtl() : 2419200L;
        String notificationImage = configProvider.getFirebase().getNotificationImageUrl();

        /* Notification */
        Notification.Builder notificationBuilder = Notification.builder();
        if(messageType.equals(MessageType.display)) {
            if (!StringUtils.isEmpty(notificationEntity.getTitle())) {
                notificationBuilder.setTitle(notificationEntity.getTitle());
            }

            if (!StringUtils.isEmpty(notificationEntity.getBody())) {
                notificationBuilder.setBody(notificationEntity.getBody());
            }

            if (!StringUtils.isEmpty(notificationImage)) {
                notificationBuilder.setImage(notificationImage);
            }
        }

        /* Android Config */
        AndroidConfig.Builder androidConfigBuilder = AndroidConfig.builder();
        androidConfigBuilder.setPriority(highPriority ? Priority.HIGH : Priority.NORMAL);
        androidConfigBuilder.setTtl(ttl * 1000);

        if(messageType.equals(MessageType.display)) {
            if (!StringUtils.isEmpty(notificationEntity.getClick_action())) {
                androidConfigBuilder.setNotification(AndroidNotification.builder().setClickAction(notificationEntity.getClick_action()).build());
            }
        }

        /* iOS config */
        ApnsConfig.Builder apnsConfigBuilder = ApnsConfig.builder();
        apnsConfigBuilder.putHeader("apns-priority", highPriority ? "10" : "5"); // 10 == high, 5 == normal, must be 5 for silent notification
        apnsConfigBuilder.putHeader("apns-expiration", Long.toString(ttl));

        if(messageType.equals(MessageType.data)) {
            apnsConfigBuilder.putHeader("apns-push-type", "background");
        }

        Aps.Builder apsBuilder = Aps.builder();
        if(messageType.equals(MessageType.data)) {
            apsBuilder.setContentAvailable(true);
        } else {
            if (!StringUtils.isEmpty(notificationEntity.getAps_category())) {
                apsBuilder.setCategory(notificationEntity.getAps_category());
            }
            apsBuilder.setMutableContent(true);
        }
        apnsConfigBuilder.setAps(apsBuilder.build());

        /* Message */
        Message.Builder messageBuilder = Message.builder();
        if(messageType.equals(MessageType.display)) {
            messageBuilder.setNotification(notificationBuilder.build());
        }
        messageBuilder
                .setAndroidConfig(androidConfigBuilder.build())
                .setApnsConfig(apnsConfigBuilder.build())
                .setToken(token);

        /* Data payload */
        if(notificationEntity.getData() == null) {
            notificationEntity.setData(new HashMap<>());
        }
        // add title, body and image if not existing in payload
        Map<String, String> dataMap = notificationEntity.getData();
        if (!StringUtils.isEmpty(notificationEntity.getTitle()) && !dataMap.containsKey("title")) {
            dataMap.put("title", notificationEntity.getTitle());
        }
        if (!StringUtils.isEmpty(notificationEntity.getBody()) && !dataMap.containsKey("body")) {
            dataMap.put("body", notificationEntity.getBody());
        }
        if (!StringUtils.isEmpty(notificationImage)) {
            dataMap.put("image", notificationImage);
        }
        messageBuilder.putAllData(dataMap);

        Message message = messageBuilder.build();
        try {
            log.debug("Sending message to {}. Message: {}", token, message);

            String mesgId = firebaseMessaging.send(message);
            log.debug("firebaseMessaging.send() - mesgId: {}", mesgId);
        } catch (FirebaseMessagingException e) {

            Map<String, Object> parameterMap = new LinkedHashMap<>();
            parameterMap.put("correlation_id", MDC.get(CORRELATION_ID_HEADER));

            IncomingHttpResponse response = e.getHttpResponse();
          if (response != null && response.getContent() != null) {
                JSONObject errorObject = new JSONObject(response.getContent());
                parameterMap.put("fcm_response", errorObject.toMap());
            }

            throw new FirebaseProblem(e, parameterMap); // https://firebase.google.cn/docs/reference/fcm/rest/v1/ErrorCode?hl=en
            // TODO: purge invalid?
            /* example error:

            {
  "error": {
    "code": 400,
    "message": "Request contains an invalid argument.",
    "status": "INVALID_ARGUMENT",
    "details": [
      {
        "@type": "type.googleapis.com/google.firebase.fcm.v1.FcmError",
        "errorCode": "INVALID_ARGUMENT"
      },
      {
        "@type": "type.googleapis.com/google.firebase.fcm.v1.ApnsError",
        "statusCode": 400,
        "reason": "BadDeviceToken"
      }
    ]
  }
}

             */
        }

    }


}
