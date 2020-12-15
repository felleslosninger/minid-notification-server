package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class NotificationRequest {
    String person_identifier;
    String app_identifier; // eg. app-id as registered as Google Play Store - "no.digdir.minid.appname"
    String title;
    String body;
    Map<String, String> data; // Arbitrary key/value payload. The key should not be a reserved word ("from", "message_type", or any word starting with "google" or "gcm").
}
