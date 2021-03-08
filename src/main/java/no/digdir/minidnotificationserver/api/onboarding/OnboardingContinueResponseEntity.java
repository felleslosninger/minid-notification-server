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
public class OnboardingContinueResponseEntity {

    @NotBlank
    @Schema(description = "The two-factor method currently in use.", example = "pin", allowableValues =  {"pin", "sms"})
    String two_factor_method;

    @Schema(description = "The pin index to use. Only set if two_factor_method=\"sms\".", example = "17")
    String pin_index;

    @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
    String state;
}
