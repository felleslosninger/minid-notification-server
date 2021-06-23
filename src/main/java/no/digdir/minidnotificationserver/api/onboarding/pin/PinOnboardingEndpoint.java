package no.digdir.minidnotificationserver.api.onboarding.pin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeaders;
import no.digdir.minidnotificationserver.integration.idporten.IdportenEntity;
import no.digdir.minidnotificationserver.service.PinOnboardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api/onboarding/pin")
@RequiredArgsConstructor
@ValidateVersionHeaders
public class PinOnboardingEndpoint {

    private final PinOnboardingService onboardingService;

    @Operation(summary = "Start app-based registration and onboarding with codes from pin-code letter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/start")
    public ResponseEntity<PinOnboardingEntity.Start.Response> start_onboarding(@RequestBody PinOnboardingEntity.Start.Request entity) {
        PinOnboardingEntity.Start.Response responseEntity = onboardingService.startOnboarding(entity);
        return ResponseEntity.ok(responseEntity);
    }

    @Operation(summary = "Continue pin-based registration and onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/continue")
    public ResponseEntity<String> continue_onboarding(@RequestBody PinOnboardingEntity.Continue.Request entity) {
        onboardingService.continueOnboarding(entity);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Finalize pin-based registration and onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/finalize")
    public ResponseEntity<IdportenEntity.TokenResponse> finalize_onboarding(@RequestBody PinOnboardingEntity.Finalize.Request entity) {
        IdportenEntity.TokenResponse responseEntity = onboardingService.finalizeOnboarding(entity);
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

}
