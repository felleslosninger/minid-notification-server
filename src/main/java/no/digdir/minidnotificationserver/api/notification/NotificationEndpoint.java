package no.digdir.minidnotificationserver.api.notification;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationRequest;
import no.digdir.minidnotificationserver.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationEndpoint {

    private final NotificationService notificationService;

    @PostMapping("/notification/send")
//    @PreAuthorize("hasAuthority('SCOPE_minid:app.send_or_something')")
    public ResponseEntity<String> send(@RequestBody NotificationRequest notificationRequest /*, @AuthenticationPrincipal Jwt accessToken */) {
        notificationService.send(notificationRequest);
        return ResponseEntity.ok("{\"status\": \"Great success!\"}");
    }

}