package no.digdir.minidnotificationserver.api.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.service.AdminContext;
import no.digdir.minidnotificationserver.service.NotificationCache;
import no.digdir.minidnotificationserver.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/authentication")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
public class AuthenticationNotificationEndpoint {

    private final NotificationService notificationService;
    private final NotificationCache notificationCache;
    private final ConfigProvider configProvider;


    @Operation(summary = "Send a notification to be relayed to minid app approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/notification/send")
    @PreAuthorize("hasAuthority('SCOPE_minid:notification.send')")
    public ResponseEntity<String> send(@RequestHeader HttpHeaders headers, @RequestBody AuthenticationNotificationEntity authenticationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        NotificationEntity notificationEntity = createNotificationEntity(authenticationEntity);
        notificationCache.putLoginAttempt(authenticationEntity.key, authenticationEntity);
        notificationService.send(notificationEntity, AdminContext.of(headers, principal));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private NotificationEntity createNotificationEntity(AuthenticationNotificationEntity authenticationEntity) {
        Map<String, String> data = new HashMap<>();
        data.put("request_id", authenticationEntity.key);
        data.put("request_type", "LOGIN");
        data.put("expiry", authenticationEntity.getLoginAttemptExpiry().toString());
        data.put("service_name", authenticationEntity.serviceProvider);
        data.put("requires_local_athentication", "true");
        return NotificationEntity.builder()
                .person_identifier(authenticationEntity.getPersonIdentifier())
                .app_identifier(configProvider.getAuthenticator().getAppIdentifier())
                .login_attempt_id(authenticationEntity.getLoginAttemptId())
                .login_attempt_counter(authenticationEntity.getLoginAttemptCounter())
                .login_attempt_expiry(authenticationEntity.getLoginAttemptExpiry())
                .title(authenticationEntity.getTitle())
                .body(authenticationEntity.getBody())
                .priority(configProvider.getAuthenticator().getPriority())
                .ttl(configProvider.getAuthenticator().getTtl())
                .aps_category("authCategory")
                .click_action("minid_auth_intent")
                .data(data)
                .build();

    }

}
