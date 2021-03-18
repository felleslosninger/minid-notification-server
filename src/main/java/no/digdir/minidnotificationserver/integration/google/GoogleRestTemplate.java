package no.digdir.minidnotificationserver.integration.google;

import no.digdir.minidnotificationserver.config.correlation.CorrelationIdInterceptor;
import no.digdir.minidnotificationserver.config.developer.DeveloperLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class GoogleRestTemplate extends RestTemplate {

    public GoogleRestTemplate(@Autowired(required = false) DeveloperLoggingRequestInterceptor loggingInterceptor,
                              CorrelationIdInterceptor correlationIdInterceptor,
                              GoogleClientErrorHandler responseErrorHandler) {
        this.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        this.setErrorHandler(responseErrorHandler);
        this.setMessageConverters(
                Arrays.asList(new FormHttpMessageConverter(),
                        new OAuth2AccessTokenResponseHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter()
                )
        );
        this.getInterceptors().add(correlationIdInterceptor);
        if(loggingInterceptor != null) { // only enabled in debug profile
            this.getInterceptors().add(loggingInterceptor);
        }
    }
}
