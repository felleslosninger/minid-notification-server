package no.digdir.minidnotificationserver.api.internal.validate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateEntity {

    @NotBlank
    @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
    String person_identifier;

    @NotBlank
    @Schema(description = "The FCM or APNs registration token.", example = "asdf1234")
    @AuditMasked
    String token;

    @NotBlank
    @Schema(description = "The login attempt counter id. Used for audit-logging.", example = "[uuid-4]")
    String login_attempt_id;

}