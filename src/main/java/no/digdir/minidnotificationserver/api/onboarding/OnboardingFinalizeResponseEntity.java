package no.digdir.minidnotificationserver.api.onboarding;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingFinalizeResponseEntity {

    @NotBlank
    @Schema(description = "Oauth2 access_token")
    String access_token;

    @Schema(description = "Oauth2 refresh_token")
    String refresh_token;

    @Schema(description = "Authentication expiry date in  ISO-8601 format.", example = "2021-02-18T10:15:30+01:00")
    String expiry;

    @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
    String state;
}
