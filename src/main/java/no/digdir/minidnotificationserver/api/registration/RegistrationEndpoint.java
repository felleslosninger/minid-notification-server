package no.digdir.minidnotificationserver.api.registration;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationEndpoint {

    private final RegistrationService registrationService;

    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @PostMapping("/register/device")
    public ResponseEntity<String> registerDevice(@RequestBody RegistrationRequest registrationRequest, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        registrationRequest.setPerson_identifier(principal.getAttribute("pid"));
        registrationService.registerDevice(registrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')")
    @DeleteMapping("/register/device/{token}")
    public ResponseEntity<String> deleteDevice(@PathVariable("token") String token, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        String personIdentifier = principal.getAttribute("pid");
        registrationService.deleteDevice(personIdentifier, token);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}