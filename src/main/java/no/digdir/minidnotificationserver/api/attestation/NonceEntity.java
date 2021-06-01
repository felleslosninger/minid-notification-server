package no.digdir.minidnotificationserver.api.attestation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;
import org.apache.commons.lang3.RandomStringUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NonceEntity {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "NonceStorage")
    public static class Storage implements Serializable {
        @NotBlank
        @Schema(description = "The FCM or APNs registration token.", example = "asdf1234")
        @AuditMasked
        String token;

        @Schema(description = "Timestamp", example = "asdf1234")
        long timestamp;

        @NotBlank
        @Schema(description = "Generated unique nonce", example = "SHA-256")
        @AuditMasked
        String nonce;

        public NonceEntity.Storage fromRequestEntity(Request nonceEntity) {
            return NonceEntity.Storage.builder()
                    .token(nonceEntity.getToken())
                    .timestamp(new Date().getTime())
                    .nonce(generateNonce())
                    .build();
        }

        public NonceEntity.Response getNonceResponse() {
            return NonceEntity.Response.builder()
                    .nonce(nonce)
                    .build();
        }

        public String generateNonce() {
            String generatedRandomString =
                    RandomStringUtils.random(8, true, true);

            return org.apache.commons.codec.digest.DigestUtils.sha256Hex(
                    token + generatedRandomString);
        }
    }

    @Data
    @Schema(name = "NonceRequest")
    public static class Request implements Serializable {
        @NotBlank
        @Schema(description = "The FCM or APNs registration token.", example = "asdf1234")
        @AuditMasked
        String token;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "NonceResponse")
    public static class Response implements Serializable {
        @NotBlank
        @AuditMasked
        @Schema(description = "Generated unique nonce", example = "SHA-256")
        String nonce;
    }

}
