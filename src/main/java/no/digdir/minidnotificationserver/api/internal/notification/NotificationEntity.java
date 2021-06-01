package no.digdir.minidnotificationserver.api.internal.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationEntity {
    // max payload is 4KB

    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
    String person_identifier;

    @NotBlank (message = "app-id e.g 'no.digdir.minid.authenticator'")
    @Schema(description = "Application identifier", example = "no.digdir.minid.authenticator")
    String app_identifier;

    @NotBlank
    @Schema(description = "The title of the notification", example = "Test notification")
    String title;

    @NotBlank
    @Schema(description = "The body of the notification", example = "Danger, Will Robinson!")
    String body;

    @Schema(description = "Priority of notification. Usage of HIGH should result in user interaction, otherwise messages may be de-prioritized if abuse pattern is detected.", example = "normal", defaultValue = "normal",  allowableValues =  {"high", "normal"})
    String priority;

    @Schema(description = "Time-to-live of notification in seconds. A value of 0 gives a best-effort of 'now or never', and is discarded if immediate delivery fails.", example = "180", defaultValue = "2419200")
    Long ttl;

    @Schema(description = "iOS category", example = "authCategory")
    String aps_category;

    @Schema(description = "Android intent", example = "MINID_AUTH_REQUEST_EVENT")
    String click_action;

    @Schema(description = "Arbitrary key/value payload. The key should not be a reserved word ('from', 'message_type', or any word starting with 'google' or 'gcm')", example = "{foo: bar, foz: baz}",
            type = "object",
            implementation = Map.class
    )
    Map<String, String> data;
}


