package no.digdir.minidnotificationserver.api.onboarding;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.OnboardingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingEndpoint {

    private final OnboardingService onboardingService;


    @Operation(summary = "Start app-based onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/start")
    public ResponseEntity<String> start_auth(@RequestHeader HttpHeaders headers, @RequestBody OnboardingStartRequestEntity entity) {
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
    @PostMapping("/continue")
    public ResponseEntity<OnboardingContinueResponseEntity> continue_auth(@RequestHeader HttpHeaders headers, @RequestBody OnboardingContinueRequestEntity entity) {
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
    @PostMapping("/finalize")
    public ResponseEntity<OnboardingFinalizeResponseEntity> finalize_auth(@RequestHeader HttpHeaders headers, @RequestBody OnboardingFinalizeRequestEntity entity) {
        OnboardingFinalizeResponseEntity responseEntity = onboardingService.finalizeAuth(entity);
        return ResponseEntity.ok(responseEntity);
    }

}
