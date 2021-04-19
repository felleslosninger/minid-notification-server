package no.digdir.minidnotificationserver.config.filters;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AdminContextFilter implements Filter {

    public static final String ADMIN_USER_ID_HEADER = "admin-user-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        String header = httpRequest.getHeader(ADMIN_USER_ID_HEADER);
        if(header != null && !header.isEmpty()){
            MDC.put("admin_user_id", header);
        }

        chain.doFilter(httpRequest, response);
    }
}
