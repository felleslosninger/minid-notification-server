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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingFinalizeRequestEntity {
    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
    String person_identifier;

    @NotBlank
    @Size(min = 5, max = 5)
    @Schema(description = "One-time-passcode", example = "12345")
    String otp;

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

    //@NotBlank
    @Size(max = 256)
    @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
    String state; // not needed for xsrf prevention when using code_challenge.

    @NotBlank
    @Size(min = 43, max = 128)
    @Schema(description = "PCKE-style code verifier", example = "M25iVXpKU3puUjFaYWg3T1NDTDQtcW1ROUY5YXlwalNoc0hhakxifmZHag")
    String code_verifier; // code_challenge_method=S256

}
