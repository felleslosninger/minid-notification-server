package no.digdir.minidnotificationserver.api.internal.authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.RequestAuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
public class RequestAuthorizationEndpoint {

    private final RequestAuthorizationService requestAuthorizationService;

    @Operation(summary = "Send a authorization request to be relayed to minid-app for approval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/request_authorization")
    public ResponseEntity<String> requestAuthorization(@RequestBody RequestAuthorizationEntity entity) {
        requestAuthorizationService.request(entity);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
