package no.digdir.minidnotificationserver.integration.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseBeans {

    private final static Logger log = LoggerFactory.getLogger(FirebaseBeans.class);


    @Value("${digdir.firebase.credentials-json}")
    private Resource credentialsResource;

    @Bean
    FirebaseMessaging firebaseMessaging() {

        try {
            InputStream credentialsStream = credentialsResource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException ex) {
            log.error("Could not initialize Firebase: ", ex);
        }

        return FirebaseMessaging.getInstance();
    }
}
