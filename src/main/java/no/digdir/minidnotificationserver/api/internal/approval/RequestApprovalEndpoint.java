package no.digdir.minidnotificationserver.api.internal.approval;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.Utils;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.service.AdminContext;
import no.digdir.minidnotificationserver.service.NotificationServerCache;
import no.digdir.minidnotificationserver.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
public class RequestApprovalEndpoint {

    private final NotificationService notificationService;
    private final NotificationServerCache cache;
    private final ConfigProvider configProvider;


    @Operation(summary = "Send a notification to be relayed to minid-app approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation, no content returned."),
            @ApiResponse(responseCode = "401", description = "Access denied due to incorrect scope or missing access token."),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/request_approval")
    public ResponseEntity<String> requestApproval(@RequestHeader HttpHeaders headers, @RequestBody RequestApprovalEntity authenticationEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        NotificationEntity notificationEntity = createNotificationEntity(authenticationEntity);
        cache.putLoginAttempt(authenticationEntity.key, authenticationEntity);
        notificationService.send(notificationEntity, AdminContext.of(headers, principal));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private NotificationEntity createNotificationEntity(RequestApprovalEntity requestApprovalEntity) {
        Map<String, String> data = new HashMap<>();
        data.put("request_id", requestApprovalEntity.key);
        data.put("request_type", "LOGIN");
        data.put("expiry", requestApprovalEntity.getLogin_attempt_expiry().format(Utils.dtf));
        data.put("service_name", requestApprovalEntity.getService_provider());
        data.put("requires_local_authentication", "true");
        return NotificationEntity.builder()
                .person_identifier(requestApprovalEntity.getPerson_identifier())
                .app_identifier(configProvider.getAuthenticator().getAppIdentifier())
                .login_attempt_id(requestApprovalEntity.getLogin_attempt_id())
                .login_attempt_counter(requestApprovalEntity.getLogin_attempt_counter())
                .login_attempt_expiry(requestApprovalEntity.getLogin_attempt_expiry())
                .title(requestApprovalEntity.getTitle())
                .body(requestApprovalEntity.getBody())
                .priority(configProvider.getAuthenticator().getPriority())
                .ttl(configProvider.getAuthenticator().getTtl())
                .aps_category("authCategory")
                .click_action("minid_auth_intent")
                .data(data)
                .build();
    }

}
