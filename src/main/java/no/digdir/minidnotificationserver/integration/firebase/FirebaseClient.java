package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.notification.NotificationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirebaseClient {

    //TODO:  MUST use exponential backoff: https://googleapis.github.io/google-http-java-client/exponential-backoff.html


    private final FirebaseMessaging firebaseMessaging;


    public void send(NotificationRequest request, String token) {

        try {
            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    /* Contains the URL of an image that is going to be downloaded on the device and displayed in a notification. JPEG, PNG, BMP have full support across platforms. Animated GIF and video only work on iOS. WebP and HEIF have varying levels of support across platforms and platform versions. */
                    .setImage("https://idporten.difi.no/error/images/svg/eid.svg") // TODO: externalize url
                    .build();

            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .setNotification(notification)
                    .putAllData(request.getData())
                    .setToken(token)
                    .build();

            // Send a message to the device corresponding to the provided
            // registration token.
            String response = firebaseMessaging.send(message);

            // Response is a message ID string.
            System.out.println("*** Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) { // TODO: propagate errors instead
            e.printStackTrace();
        }

    }

}
