package no.digdir.minidnotificationserver.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationEndpoint {

    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()") // requires a valid access_token
    public ResponseEntity<String> register() {
        // call /tokeninfo and extract person_identifier
        // save person_identifier and message_token in db
        return ResponseEntity.ok("{\"status\": \"Great success!\"}");
    }

}