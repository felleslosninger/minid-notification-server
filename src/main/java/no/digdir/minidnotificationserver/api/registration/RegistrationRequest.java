package no.digdir.minidnotificationserver.api.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank
    String app_identifier; // eg. app-id as registered as Google Play Store - "no.digdir.minid.appname"

    @NotBlank
    String token;          // FCM registration token

    @NotBlank
    String description;

    @NotBlank
    String os;

    @NotBlank
    String os_version;
}