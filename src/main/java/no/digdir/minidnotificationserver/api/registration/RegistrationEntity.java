package no.digdir.minidnotificationserver.api.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationEntity {

    @NotBlank (message = "app-id e.g 'no.digdir.minid.appname'")
    @Schema(description = "An application identifier.", example = "no.digdir.minid.authenticator")
    @Size(max = 64)
    String app_identifier;

    @NotBlank
    @Schema(description = "The version of the app.", example = "1.0.5")
    @Size(max = 8)
    String app_version;

    @NotBlank
    @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
    @Size(max = 4096)
    String token;

    @JsonIgnore
    String apns_token;

    @NotBlank
    @Schema(description = "The operating system of the unit.", example = "Android")
    @Size(max = 64)
    String os;

    @NotBlank
    @Schema(description = "The version of the operating system.", example = "11")
    @Size(max = 64)
    String os_version;

    public static RegistrationEntity from(RegistrationDevice device) {
        return RegistrationEntity.builder()
                .app_identifier(device.getAppIdentifier())
                .app_version(device.getAppVersion())
                .token(device.getFcmToken())
                .os(device.getOs())
                .os_version(device.getOsVersion())
                .build();
    }
}