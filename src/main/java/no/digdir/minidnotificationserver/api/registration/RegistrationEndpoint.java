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
        registrationService.registerDevice(principal.getAttribute("pid"), registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/register/device/{token}")
    public ResponseEntity<String> updateDevice(@PathVariable("token") String token, @RequestBody RegistrationRequest registrationRequest) {
        registrationService.updateDevice(token, registrationRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/register/device/{token}")
    public ResponseEntity<String> deleteDevice(@PathVariable("token") String token) {
        registrationService.deleteDevice(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}