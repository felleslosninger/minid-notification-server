package no.digdir.minidnotificationserver.config;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

public class PrincipalContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof BearerTokenAuthentication) {
            String pid = (String) ((BearerTokenAuthentication) authentication).getTokenAttributes().get("pid");
            List<String> scopesList = (List<String>) ((BearerTokenAuthentication) authentication).getTokenAttributes().get("scope");
            String scopes = String.join(", ", scopesList);
            MDC.put("principal", pid);
            MDC.put("scopes", scopes);
        }

        chain.doFilter(request, response);
    }
}