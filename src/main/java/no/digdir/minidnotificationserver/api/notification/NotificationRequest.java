package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationRequest {
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

    @JsonIgnore
    @Schema(description = "Arbitrary key/value payload. The key should not be a reserved word ('from', 'message_type', or any word starting with 'google' or 'gcm')", example = "{foo: bar}")
    Map<String, String> data;

    @JsonAnySetter
    public void setData(Map<String, String> data) {
        // TODO: add validation
        this.data = data;
    }
}
