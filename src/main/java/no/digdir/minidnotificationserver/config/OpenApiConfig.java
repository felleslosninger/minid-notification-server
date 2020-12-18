package no.digdir.minidnotificationserver.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "notification_auth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
                        tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
                        scopes = {
                                @OAuthScope(name = "openid"),
                                @OAuthScope(name = "profile"),
                                @OAuthScope(name = "minid:notification.send", description = "Required for sending messages to mobile users.")
                        }
                ))
)

@SecurityScheme(
        name = "registration_auth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
                        tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
                        scopes = {
                                @OAuthScope(name = "openid"),
                                @OAuthScope(name = "profile"),
                                @OAuthScope(name = "minid:app.register", description = "Required for registering a device.")
                        }
                ))
)

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${project.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("MinID Notification Server")
                        .version(appVersion)
                        .description("Service for registering the users and their units of the MinID mobile app, and sending notifications to them.")
                );
    }


}
