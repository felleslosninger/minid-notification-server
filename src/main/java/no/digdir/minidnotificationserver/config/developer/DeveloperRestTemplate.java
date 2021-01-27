package no.digdir.minidnotificationserver.config.developer;

import no.digdir.minidnotificationserver.config.CorrelationIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Profile({"dev"}) /* Only enabled when 'dev' profile is active */
@Component("RestTemplate")
public class DeveloperRestTemplate extends RestTemplate {

    public DeveloperRestTemplate(@Autowired(required = false) DeveloperLoggingRequestInterceptor loggingInterceptor,
                                 CorrelationIdInterceptor correlationIdInterceptor) {
        this.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        this.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.setMessageConverters(
            Arrays.asList(new FormHttpMessageConverter(),
                new OAuth2AccessTokenResponseHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()
            )
        );
        this.getInterceptors().add(correlationIdInterceptor);
        if(loggingInterceptor != null) { // only enabled in dev profile
            this.getInterceptors().add(loggingInterceptor);
        }
    }
}
