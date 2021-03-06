package no.digdir.minidnotificationserver.integration.google;

import no.digdir.minidnotificationserver.config.correlation.CorrelationIdInterceptor;
import no.digdir.minidnotificationserver.config.developer.DeveloperLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleRestTemplate extends RestTemplate {

    public GoogleRestTemplate(@Autowired(required = false) DeveloperLoggingRequestInterceptor loggingInterceptor,
                              CorrelationIdInterceptor correlationIdInterceptor,
                              GoogleClientErrorHandler errorHandler) {
        this.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        this.getInterceptors().add(correlationIdInterceptor);
        this.setErrorHandler(errorHandler);
        if(loggingInterceptor != null) { // only enabled in debug profile
            this.getInterceptors().add(loggingInterceptor);
        }
    }
}
