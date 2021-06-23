package no.digdir.minidnotificationserver.api.registration.passport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import no.digdir.minidnotificationserver.api.device.IDeviceEntity;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PassportEntity {
    @Data
    public static class Start {
        @Data
        @Schema(name = "PassportStartRequest")
        public static class Request implements Serializable, IDeviceEntity {
            @NotBlank
            @Size(max = 4096)
            @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
            @AuditMasked
            String token;

            @JsonIgnore
            @AuditMasked
            String apns_token;

            @Schema(description = "APNs sandbox.", example = "false", defaultValue = "false")
            boolean apns_sandbox = false;

            @NotBlank(message = "app-id e.g 'no.digdir.minid.appname'")
            @Schema(description = "An application identifier.", example = "no.digdir.minid.authenticator")
            @Size(max = 64)
            String app_identifier;

            @NotBlank
            @Schema(description = "The version of the app.", example = "1.0.5")
            @Size(max = 8)
            String app_version;

            @NotBlank
            @Size(max = 64)
            @Schema(description = "The operating system of the unit.", example = "Android")
            String os;

            @NotBlank
            @Schema(description = "The version of the operating system.", example = "11")
            @Size(max = 64)
            String os_version;

            @NotBlank
            @Size(min = 43, max = 128)
            @Schema(description = "PCKE-style code challenge, with code_challenge_method=S256", example = "qjrzSW9gMiUgpUvqgEPE4_-8swvyCtfOVvg55o5S_es")
            String code_challenge; // code_challenge_method=S256

            @JsonIgnore
            String login_key;

            @JsonIgnore
            String auth_req_id;

            @JsonIgnore
            ZonedDateTime expiry;
        }

        @Data
        @Builder
        @Schema(name="PassportStartResponse")
        public static class Response {
            @NotBlank
            @Schema(description = "The authorization request id.", example = "UjFaYWg3T1NDTDQtcW1ROUY5YX")
            String auth_req_id;
        }

    }

    @Data
    public static class Finalize {
        @Data
        @Schema(name = "PassportFinalizeRequest")
        public static class Request {
            @NotBlank
            @Size(max = 4096)
            @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
            @AuditMasked
            String token;

            @JsonIgnore
            @AuditMasked
            String apns_token;

            @NotBlank
            @Size(max = 64)
            @Schema(description = "The operating system of the unit.", example = "Android")
            String os;

            @NotBlank
            @Size(min = 36, max = 36)
            @Schema(description = "The login key sent through firebase.", example = "[uuid v4]")
            String login_key; // uuid v4

            @NotBlank
            @Size(min = 43, max = 128)
            @Schema(description = "PCKE-style code verifier", example = "M25iVXpKU3puUjFaYWg3T1NDTDQtcW1ROUY5YXlwalNoc0hhakxifmZHag")
            String code_verifier; // code_challenge_method=S256
        }

    }

}
