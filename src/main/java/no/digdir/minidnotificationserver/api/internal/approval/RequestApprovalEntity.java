package no.digdir.minidnotificationserver.api.internal.approval;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestApprovalEntity {
    // max payload is 4KB

    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
    String person_identifier;

    @Schema(description = "The login attempt id", example = "[uuid-4]")
    String login_attempt_id;

    @Schema(description = "The login attempt counter", example = "1")
    Integer login_attempt_counter;

    @Schema(description = "The expiry time of the login attempt in ISO-8601 format.", example = "2021-02-18T10:15:30+01:00")
    ZonedDateTime login_attempt_expiry;

    @NotBlank
    @Schema(description = "The title of the notification", example = "Test notification")
    String title;

    @NotBlank
    @Schema(description = "The body of the notification", example = "Lengre tekst")
    String body;

    @NotBlank
    @Schema(description = "The service provider for the login", example = "NAV, Vigo")
    String service_provider;

    @JsonIgnore
    String key = login_attempt_id + "_" + login_attempt_counter;

}


