package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.*;
import no.digdir.minidnotificationserver.api.notification.NotificationRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirebaseClient {

    //TODO:  MUST use exponential backoff: https://googleapis.github.io/google-http-java-client/exponential-backoff.html


    public void send(NotificationRequest request, List<String> tokenList) {

        try {

            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getMessage())
                    .setImage("https://idporten.difi.no/error/images/svg/eid.svg")
                    .build();

            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(tokenList)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    public void send(NotificationRequest request, String token) {

        try {
            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getMessage())
                    .setImage("https://idporten.difi.no/error/images/svg/eid.svg")
                    .build();

            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .setNotification(notification)
                    .setToken(token)
                    .build();

            // Send a message to the device corresponding to the provided
            // registration token.
            String response = FirebaseMessaging.getInstance().send(message);

            // Response is a message ID string.
            System.out.println("*** Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

    }

}
