package no.digdir.minidnotificationserver.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "digdir", ignoreUnknownFields = false)
public class ConfigProvider implements InitializingBean {

    private Firebase firebase = new Firebase();

    @Data
    public static class Cors {
        private String allowedOrigin;
        private List<String> allowedHeaders;
        private List<String> allowedMethods;
        private List<String> exposedHeaders;
        private Boolean allowCredentials;
        private Long maxAge;
    }

    @Data
    public static class Firebase {
        @NotBlank
        private String credentialsJson;

        /**
         * Contains the URL of an image that is going to be downloaded on the device and displayed in a notification.
         * JPEG, PNG, BMP have full support across platforms. Animated GIF and video only work on iOS. WebP and HEIF
         * have varying levels of support across platforms and platform versions.
         */
        @NotBlank
        private String notificationImageUrl;
    }

    @Override
    public void afterPropertiesSet() {
//        getReactApp().setLoginFailureUrl(getReactApp().getUrl() + "login/error");
    }

}