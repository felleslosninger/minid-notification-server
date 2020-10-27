package no.digdir.minidnotificationserver.api.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationEndpoint {

    @GetMapping("/send")
    @PreAuthorize("hasAuthority('SCOPE_minid:app.send_or_something')")
    public ResponseEntity<String> send() {
        // call service layer and send message to given person_identifier
        return ResponseEntity.ok("{\"status\": \"Great success!\"}");
    }

}