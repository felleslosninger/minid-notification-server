package no.digdir.minidnotificationserver.api.registration.passport;

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
import no.digdir.minidnotificationserver.service.PassportOnboardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api/register/passport")
@RequiredArgsConstructor
@ValidateVersionHeaders

public class PassportEndpoint {

    private final PassportOnboardingService passportOnboardingService;

    @Operation(summary = "Start passport onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/start")
    public ResponseEntity<PassportEntity.Start.Response> start(@RequestBody PassportEntity.Start.Request requestEntity) {
        PassportEntity.Start.Response responseEntity = passportOnboardingService.start(requestEntity);
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @Operation(summary = "Finalize passport onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/finalize")
    public ResponseEntity<IdportenEntity.TokenResponse> finalize(@RequestBody PassportEntity.Finalize.Request requestEntity) {
        IdportenEntity.TokenResponse responseEntity = passportOnboardingService.finalize(requestEntity);
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }


}