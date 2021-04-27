package no.digdir.minidnotificationserver.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Validated
@ConfigurationProperties(prefix = "digdir", ignoreUnknownFields = false)
public class ConfigProvider implements InitializingBean {
    private AppVersions appVersions = new AppVersions();
    private Firebase firebase = new Firebase();
    private Audit audit = new Audit();
    private Proxy proxy = new Proxy();
    private GoogleApi googleApi = new GoogleApi();
    private Authenticator authenticator = new Authenticator();
    private MinidAuthenticationService minidAuthenticationService = new MinidAuthenticationService();
    private MinidBackendService minidBackendService = new MinidBackendService();
    private IdportenOidcProvider idportenOidcProvider = new IdportenOidcProvider();

    @Data
    public static class AppVersions {
        private DeviceVersions ios;
        private DeviceVersions android;
    }

    @Data
    public static class DeviceVersions {
        @NotBlank
        private String latest;
        @NotBlank
        private String required;
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

    @Data
    public static class Audit {
        @NotBlank
        private String logDir;

        @NotBlank
        private String logFile;
    }

    @Data
    public static class Proxy {
        private boolean enabled = false;
        private String host;
        private String port;
        private String skipHosts;
    }

    @Data
    public static class GoogleApi {
        private boolean sandbox = true;
        private boolean sandboxClientOverride = false;
        private String bundleId;
        private String authKey;
    }

    @Data
    public static class Authenticator {
        private long expiry; // expiry of onboarding auth process in seconds.
        private String appIdentifier;
        private String priority;
        private long ttl;
        private String onboardingCategory;
        private String authenticationApsCategory;
        private String authenticationClickAction;

    }

    @Data
    public static class MinidAuthenticationService {
        private String url;
    }

    @Data
    public static class MinidBackendService {
        private String url;
    }

    @Data
    public static class IdportenOidcProvider {
        private String url;
        private String oidcClientId;    // basic auth username for authorize/token endpoint
        private String oidcClientSecret; // basic auth password for authorize/token endpoint
        private String clientId;      // service client-id
        private Set<String> scopes;
        private String redirectUri;
    }

    @Override
    public void afterPropertiesSet() {
//        getReactApp().setLoginFailureUrl(getReactApp().getUrl() + "login/error");
    }

}
