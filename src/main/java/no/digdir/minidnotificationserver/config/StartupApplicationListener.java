package no.digdir.minidnotificationserver.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final static Logger log = LoggerFactory.getLogger(StartupApplicationListener.class);

    @Value("${digdir.firebase.credentials-json}")
    private Resource credentialsResource;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        try {
            InputStream credentialsStream = credentialsResource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException ex) {
            log.error("Could not initialize Firebase: ", ex);
        }

    }
}