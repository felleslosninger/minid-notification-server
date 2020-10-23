package no.digdir.minidnotificationserver.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "digdir", ignoreUnknownFields = false)
public class ConfigProvider implements InitializingBean {

    @Data
    public static class Cors {
        private String allowedOrigin;
        private List<String> allowedHeaders;
        private List<String> allowedMethods;
        private List<String> exposedHeaders;
        private Boolean allowCredentials;
        private Long maxAge;
    }


    @Override
    public void afterPropertiesSet() {
//        getReactApp().setLoginFailureUrl(getReactApp().getUrl() + "login/error");
    }

}