package no.digdir.minidnotificationserver.integration.idporten;

import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.config.correlation.CorrelationIdInterceptor;
import no.digdir.minidnotificationserver.config.developer.DeveloperLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class IdportenRestTemplate extends RestTemplate {


    public IdportenRestTemplate(@Autowired(required = false) DeveloperLoggingRequestInterceptor loggingInterceptor,
                                CorrelationIdInterceptor correlationIdInterceptor,
                                ConfigProvider configProvider) {
        this.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        this.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.setMessageConverters(
                Arrays.asList(new FormHttpMessageConverter(),
                        new OAuth2AccessTokenResponseHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter()
                )
        );
        String username = configProvider.getIdportenOidcProvider().getOidcClientId();
        String password = configProvider.getIdportenOidcProvider().getOidcClientSecret();
        this.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
        this.getInterceptors().add(correlationIdInterceptor);
        if(loggingInterceptor != null) { // only enabled in debug profile
            this.getInterceptors().add(loggingInterceptor);
        }
    }
}
