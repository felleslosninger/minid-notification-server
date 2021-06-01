package no.digdir.minidnotificationserver.api.attestation.android;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttestationEntity {

    @Data
    @Schema(name = "AttestationRequest")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request implements Serializable {
        @NotBlank
        @Schema(description = "Attestation JWT", example = "random-jwt-string")
        String attestationJWT;

        @NotBlank
        @Schema(description = "The FCM or APNs registration token.", example = "asdf1234")
        @AuditMasked
        String token;
    }

    @Data
    @NoArgsConstructor
    @Schema(name = "AttestationResponse")
    public static class Response implements Serializable {
        @NotBlank
        @Schema(description = "Attestation JWT", example = "random-jwt-string")
        Boolean verified;
    }

}
