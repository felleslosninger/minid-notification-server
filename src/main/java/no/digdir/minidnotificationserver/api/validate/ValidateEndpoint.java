package no.digdir.minidnotificationserver.api.validate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.AdminContext;
import no.digdir.minidnotificationserver.service.ValidationService;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
public class ValidateEndpoint {

    private final ValidationService validationService;

    @Operation(summary = "Verify pid and token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @PreAuthorize("hasAuthority('SCOPE_minid:notification.send')")
    @PostMapping("/validate")
    public ResponseEntity<String> registerDevice(@RequestHeader HttpHeaders headers, @RequestBody ValidateEntity validateEntity, @AuthenticationPrincipal Jwt accessToken) {
        boolean result = validationService.validate(validateEntity, AdminContext.of(headers, accessToken));
        return ResponseEntity.status(HttpStatus.OK).body(new JSONObject().put("result", result).toString());
    }

}