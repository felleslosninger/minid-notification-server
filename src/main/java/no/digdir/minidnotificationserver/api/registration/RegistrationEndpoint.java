package no.digdir.minidnotificationserver.api.registration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "registration_auth")
public class RegistrationEndpoint {

    private final RegistrationService registrationService;

    @Operation(summary = "Register or update a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device registered."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @PostMapping("/register/device")
    public ResponseEntity<RegistrationEntity> registerDevice(@RequestBody RegistrationEntity registrationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        RegistrationEntity device = registrationService.upsertDevice(principal.getAttribute("pid"), registrationEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }



    @Operation(summary = "Delete existing device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @DeleteMapping("/register/device")
    public ResponseEntity<RegistrationEntity> deleteDevice(@RequestBody RegistrationEntity registrationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        registrationService.deleteDevice(principal.getAttribute("pid"), registrationEntity);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}