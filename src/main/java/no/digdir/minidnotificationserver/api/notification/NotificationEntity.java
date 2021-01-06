package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationEntity {
    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(description = "Person identifier - 11 digits.", example = "01030099326")
    String person_identifier;

    @NotBlank (message = "app-id e.g 'no.digdir.minid.appname'")
    @Schema(description = "Application identifier", example = "no.digdir.minid.app")
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

    @Schema(description = "iOS category", example = "MINID_AUTH_CATEGORY")
    String aps_category;

    @Schema(description = "Android intent", example = "minid_auth_intent")
    String click_action;

    @Schema(description = "Arbitrary key/value payload. The key should not be a reserved word ('from', 'message_type', or any word starting with 'google' or 'gcm')", example = "{foo: bar, foz: baz}",
            type = "object",
            implementation = Map.class
    )
    Map<String, String> data;

//    @JsonAnySetter
//    public void setData(Map<String, String> data) {
//        // TODO: add validation
//        this.data = data;
//    }
}

// max payload is 4KB
