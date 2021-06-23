package no.digdir.minidnotificationserver.api.registration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeaders;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.aspect.version.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "registration_auth")
@ValidateVersionHeaders
public class RegistrationEndpoint {

    private final DeviceService deviceService;
    private final MinIdBackendClient minIdBackendClient;


    @Operation(summary = "Register or update a device", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device registered."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @PostMapping("/register/device")
    public ResponseEntity<DeviceEntity> registerDevice(@RequestBody DeviceEntity deviceEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        DeviceEntity device = deviceService.upsertDevice(principal.getAttribute("pid"), deviceEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @Operation(summary = "Delete existing device", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @DeleteMapping("/register/device")
    public ResponseEntity<DeviceEntity> deleteDevice(@RequestBody DeviceEntity deviceEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        String personIdentifier = principal.getAttribute("pid");
        deviceService.deleteByAppId(personIdentifier, deviceEntity);
        minIdBackendClient.setPreferredTwoFactorMethod(personIdentifier, "pin"); // TODO: temporary reset to 'pin'
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}