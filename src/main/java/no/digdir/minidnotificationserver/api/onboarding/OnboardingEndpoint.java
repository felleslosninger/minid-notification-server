package no.digdir.minidnotificationserver.api.onboarding;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.ValidateVersionHeaders;
import no.digdir.minidnotificationserver.service.OnboardingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@ValidateVersionHeaders
public class OnboardingEndpoint {

    private final OnboardingService onboardingService;


    @Operation(summary = "Start app-based onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/start")
    public ResponseEntity<String> start_auth(@RequestBody OnboardingStartRequestEntity entity) {
        // TODO: remove me - testing only
        if(true) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        onboardingService.startAuth(entity);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Continue app-based onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Quarantined for 60 minutes")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/continue")
    public ResponseEntity<OnboardingContinueResponseEntity> continue_auth(@RequestBody OnboardingContinueRequestEntity entity) {
        OnboardingContinueResponseEntity responseEntity = onboardingService.continueAuth(entity);
        return ResponseEntity.ok(responseEntity);
    }

    @Operation(summary = "Finalize app-based onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Quarantined for 60 minutes")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/finalize")
    public ResponseEntity<OnboardingFinalizeResponseEntity> finalize_auth(@RequestBody OnboardingFinalizeRequestEntity entity) {
        OnboardingFinalizeResponseEntity responseEntity = onboardingService.finalizeAuth(entity);
        return ResponseEntity.ok(responseEntity);
    }

}
