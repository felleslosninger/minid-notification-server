package no.digdir.minidnotificationserver.api.notification;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationEndpoint {

    private final NotificationService notificationService;

    @PostMapping("/notification/send")
    @PreAuthorize("hasAuthority('SCOPE_minid:notification.send')")
    public ResponseEntity<String> send(@RequestBody NotificationRequest notificationRequest, @AuthenticationPrincipal Jwt accessToken) {
        notificationService.send(notificationRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}