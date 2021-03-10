package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationNotificationEntity {
    // max payload is 4KB

    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
    @JsonProperty("person_identifier")
    String personIdentifier;

    @Schema(description = "The login attempt id", example = "[uuid-4]")
    @JsonProperty("login_attempt_id")
    String loginAttemptId;

    @Schema(description = "The login attempt counter", example = "1")
    @JsonProperty("login_attempt_counter")
    Integer loginAttemptCounter;

    @Schema(description = "The expiry time of the login attempt in ISO-8601 format.", example = "2021-02-18T10:15:30+01:00")
    @JsonProperty("login_attempt_expiry")
    ZonedDateTime loginAttemptExpiry;

    @NotBlank
    @Schema(description = "The title of the notification", example = "Test notification")
    String title;

    @NotBlank
    @Schema(description = "The body of the notification", example = "Lengre tekst")
    String body;

    @NotBlank
    @Schema(description = "The service provider for the login", example = "NAV, Vigo")
    String serviceProvider;


    public String key = loginAttemptId + "-" + loginAttemptCounter;

}


