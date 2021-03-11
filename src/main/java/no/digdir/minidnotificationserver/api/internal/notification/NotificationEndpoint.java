package no.digdir.minidnotificationserver.api.internal.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.AdminContext;
import no.digdir.minidnotificationserver.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
public class NotificationEndpoint {

    private final NotificationService notificationService;


    @Operation(summary = "Send a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/notification/send")
    @PreAuthorize("hasAuthority('SCOPE_minid:notification.send')")
    public ResponseEntity<String> send(@RequestHeader HttpHeaders headers, @RequestBody NotificationEntity notificationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        notificationService.send(notificationEntity, AdminContext.of(headers, principal));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}