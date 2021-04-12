package no.digdir.minidnotificationserver.api.device;

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
import no.digdir.minidnotificationserver.api.ValidateVersionHeaders;
import no.digdir.minidnotificationserver.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@ValidateVersionHeaders
public class DeviceEndpoint {

    private final DeviceService deviceService;

    @Operation(summary = "Update existing device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device modified successfully."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect authorization header."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PreAuthorize("hasAuthority('APP_DEVICE')")
    @PostMapping("/device")
    public ResponseEntity<DeviceEntity> updateDevice(@RequestBody DeviceEntity deviceEntity, @AuthenticationPrincipal String personIdentifier) { // TODO: use ssn validator
        DeviceEntity device = deviceService.update(personIdentifier, deviceEntity);
        return ResponseEntity.status(HttpStatus.OK).body(device);
    }

    @Operation(summary = "Delete existing device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect authorization header."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PreAuthorize("hasAuthority('APP_DEVICE')")
    @DeleteMapping("/device")
    public ResponseEntity<String> deleteDevice(@AuthenticationPrincipal String personIdentifier, Authentication authentication) { // TODO: use ssn validator
        if(authentication instanceof PreAuthenticatedAuthenticationToken) {
            String token = ((Map<String, String>) authentication.getDetails()).get("token");
            deviceService.delete(personIdentifier, token);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}