package no.digdir.minidnotificationserver.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AppTokenAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {


    public AppTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Optional<String> authzHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION)); // Authorization: AppToken [fcmToken or apnsToken]

        if(authzHeader.isPresent() && authzHeader.get().startsWith("AppToken ")) {
            return authzHeader.get().split("AppToken ")[1];
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
