package no.digdir.minidnotificationserver.api.onboarding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingContinueRequestEntity {

    @NotBlank
    @Size(max = 4096)
    @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
    String token;

    @JsonIgnore
    String apns_token;

    @NotBlank
    @Size(max = 64)
    @Schema(description = "The operating system of the unit.", example = "Android")
    String os;

    @NotBlank
    @Size(min=36, max = 36)
    String login_key; // uuid v4

}
