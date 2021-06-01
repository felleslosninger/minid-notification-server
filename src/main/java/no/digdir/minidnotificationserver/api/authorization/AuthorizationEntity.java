package no.digdir.minidnotificationserver.api.authorization;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.api.domain.TokenEntity;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationEntity implements TokenEntity {

    @NotBlank
    @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
    @Size(max = 4096)
    @AuditMasked
    String token;

    @NotBlank
    @Schema(description = "The operating system of the unit.", example = "Android", allowableValues =  {"Android", "iOS"})
    @Size(max = 64)
    String os;

    @Schema(description = "The login attempt id", example = "[uuid-4]")
    String login_attempt_id;

    @Schema(description = "The login attempt counter", example = "1")
    Integer login_attempt_counter;
}


