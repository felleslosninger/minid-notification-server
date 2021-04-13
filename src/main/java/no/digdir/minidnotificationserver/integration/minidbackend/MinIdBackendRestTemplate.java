package no.digdir.minidnotificationserver.integration.minidbackend;

import no.digdir.minidnotificationserver.config.correlation.CorrelationIdInterceptor;
import no.digdir.minidnotificationserver.config.developer.DeveloperLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MinIdBackendRestTemplate extends RestTemplate {

    public MinIdBackendRestTemplate(@Autowired(required = false) DeveloperLoggingRequestInterceptor loggingInterceptor,
                                    CorrelationIdInterceptor correlationIdInterceptor,
                                    MinidBackendErrorHandler errorHandler) {

        // needed for PATCH
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        this.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));

        this.getInterceptors().add(correlationIdInterceptor);
        this.setErrorHandler(errorHandler);
        if(loggingInterceptor != null) { // only enabled in debug profile
            this.getInterceptors().add(loggingInterceptor);
        }

    }
}
