package no.digdir.minidnotificationserver.api.onboarding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingEntity {

    @Data
    public static class Start {
        @Data
        @Schema(name="StartRequest")
        public static class Request implements Serializable {
            @NotBlank
            @Size(min = 11, max = 11)
            @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
            String person_identifier;

            @NotBlank
            @Size(min = 6, max = 64)
            @Schema(description = "Password.", example = "password01")
            String password;

            @NotBlank
            @Size(max = 4096)
            @Schema(description = "The FCM or APNs registration token (APNs format is assumed if 'os=ios').", example = "asdf1234")
            @AuditMasked
            String token;

            @JsonIgnore
            @AuditMasked
            String apns_token;

            @NotBlank (message = "app-id e.g 'no.digdir.minid.appname'")
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

            @Size(max = 256)
            @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
            String state; // not needed for xsrf prevention when using code_challenge.

            @NotBlank
            @Size(min = 43, max = 128)
            @Schema(description = "PCKE-style code challenge, with code_challenge_method=S256", example = "qjrzSW9gMiUgpUvqgEPE4_-8swvyCtfOVvg55o5S_es")
            String code_challenge; // code_challenge_method=S256

            @JsonIgnore
            String login_key;

            @JsonIgnore
            ZonedDateTime expiry;
        }

    }

    @Data
    public static class Continue {
        @Data
        @Schema(name="ContinueRequest")
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
            @Size(min=36, max = 36)
            String login_key; // uuid v4
        }

        @Data
        @Builder
        @Schema(name="ContinueResponse")
        public static class Response {
            @NotBlank
            @Schema(description = "The two-factor method currently in use.", example = "pin", allowableValues =  {"pin", "sms", "app"})
            String two_factor_method;

            @Schema(description = "The pin index to use. Only set if two_factor_method=\"pin\".", example = "17")
            Integer pin_index;

            @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
            String state;
        }
    }

    @Data
    public static class Finalize {
        @Data
        @Schema(name="FinalizeRequest")
        public static class Request {
            @NotBlank
            @Size(min = 11, max = 11)
            @Schema(description = "Person identifier - 11 digits.", example = "26079490775")
            String person_identifier;

            @Size(min = 5, max = 5)
            @Schema(description = "One-time-code from SMS or pincode letter", example = "12345")
            String otc;

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

            //@NotBlank
            @Size(max = 256)
            @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
            String state; // not needed for xsrf prevention when using code_challenge.

            @NotBlank
            @Size(min = 43, max = 128)
            @Schema(description = "PCKE-style code verifier", example = "M25iVXpKU3puUjFaYWg3T1NDTDQtcW1ROUY5YXlwalNoc0hhakxifmZHag")
            String code_verifier; // code_challenge_method=S256
        }

        @Data
        @Builder
        @Schema(name="FinalizeResponse")
        public static class Response {
            @NotBlank
            @Schema(description = "Oauth2 access_token")
            String access_token;

            @Schema(description = "Oauth2 refresh_token")
            String refresh_token;

            @Schema(description = "Authentication expiry date in  ISO-8601 format.", example = "2021-02-18T10:15:30Z")
            String expiry;

            @Schema(description = "The client application state.", example = "[any-unguessable-random-string]")
            String state;
        }
    }

    @Data
    @Builder
    public static class Verification {
        String requestUrn;
        Integer pinCodeIndex;
        String twoFactorMethod;
    }


}
