package no.digdir.minidnotificationserver.config;

import no.difi.resilience.CorrelationId;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorrelationIdHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        CorrelationId.set(httpRequest.getHeader(CorrelationId.CORRELATION_ID_HEADER));
        httpResponse.setHeader(CorrelationId.CORRELATION_ID_HEADER, CorrelationId.get());

        chain.doFilter(httpRequest, response);
    }

}
