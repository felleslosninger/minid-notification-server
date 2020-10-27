package no.digdir.minidnotificationserver.api.registration;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationEndpoint {

    private final RegistrationService registrationService;

    //@PreAuthorize("hasAuthority('SCOPE_messages')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest, @AuthenticationPrincipal Jwt accessToken) {
        // BearerTokenAuthentication authentication
        // @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal

        registrationRequest.setPerson_identifier(accessToken.getClaim("pid"));
        registrationService.registerDevice(registrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}