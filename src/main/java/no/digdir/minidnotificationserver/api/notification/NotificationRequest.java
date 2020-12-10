package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class NotificationRequest {
    String person_identifier;
    String title;
    String message;
}
