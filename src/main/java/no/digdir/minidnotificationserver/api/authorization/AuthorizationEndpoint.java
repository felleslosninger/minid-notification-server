package no.digdir.minidnotificationserver.api.authorization;

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
import no.digdir.minidnotificationserver.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api/authorization")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@ValidateVersionHeaders
public class AuthorizationEndpoint {
    private final AuthorizationService authorizationService;

    @Operation(summary = "Approve request for authorization to app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful approval, no content returned."),
            @ApiResponse(responseCode = "400", description = "Client error."),
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    public ResponseEntity<String> approve(@RequestBody AuthorizationEntity authorizationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        String personIdentifier = principal.getAttribute("pid");
        authorizationService.authorize(personIdentifier, authorizationEntity, AuthorizationService.AuthAction.APPROVE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Reject request for authorization to app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful rejection, no content returned."),
            @ApiResponse(responseCode = "400", description = "Client error."),
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/reject")
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    public ResponseEntity<String> reject(@RequestBody AuthorizationEntity authorizationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        String personIdentifier = principal.getAttribute("pid");
        authorizationService.authorize(personIdentifier, authorizationEntity, AuthorizationService.AuthAction.REJECT);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
