package no.digdir.minidnotificationserver.api.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank (message = "app-id e.g 'no.digdir.minid.appname'")
    @Schema(description = "An application identifier.", example = "no.digdir.minid.app")
    String app_identifier;

    @NotBlank
    @Schema(description = "The version of the app.", example = "1.0.5")
    String app_version;

    @NotBlank
    @Schema(description = "The FCM registration token.", example = "asdf1234")
    String token;

    @NotBlank
    @Schema(description = "Description. Not sure we need this field?", example = "A description.")
    String description;

    @NotBlank
    @Schema(description = "The operating system of the unit.", example = "Android")
    String os;

    @NotBlank
    @Schema(description = "The version of the operating system.", example = "11")
    String os_version;
}