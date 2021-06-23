package no.digdir.minidnotificationserver.api.onboarding.pin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import no.digdir.minidnotificationserver.api.device.IDeviceEntity;
import no.digdir.minidnotificationserver.api.domain.TokenEntity;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIDUserEntity;
import no.digdir.minidnotificationserver.logging.audit.AuditMasked;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PinOnboardingEntity {

    @Data
    public static class Start {
        @Data
        @Schema(name="PinStartRequest")
        public static class Request implements Serializable, TokenEntity, IDeviceEntity {
            @NotBlank
            @Size(min = 11, max = 11)
            @Schema(description = "Person identifier - 11 digits.", example = "25079400680")
            String person_identifier; // TODO: must implement validation !!!

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
            @Schema(description = "The operating system of the unit.", example = "Android", allowableValues =  {"Android", "iOS"})
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
            ZonedDateTime expiry;

            @JsonIgnore
            MinIDUserEntity.Response minIDUser;

            @JsonIgnore
            String pin; // maybe this belongs somewhere else - only kept here for verification

            @JsonIgnore
            String pin2; // maybe this belongs somewhere else - only kept here for verification
        }

        @Data
        @Builder
        @Schema(name="PinStartResponse")
        public static class Response {
            @Schema(description = "The first pin index to use.", example = "4")
            Integer pin_index;

            @Schema(description = "The second pin index to use.", example = "5")
            Integer pin_index_2;
        }

    }


    @Data
    public static class Continue {
        @Data
        @Schema(name = "PinContinueRequest")
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

            @NotBlank
            @Size(min = 5, max = 5)
            @Schema(description = "Pin code nr 1.", example = "12345")
            String pin;

            @NotBlank
            @Size(min = 5, max = 5)
            @Schema(description = "Pin code nr 2.", example = "12345")
            String pin_2;
        }

    }


    @Data
    public static class Finalize {
        @Data
        @Schema(name = "PinFinalizeRequest")
        public static class Request {

            @NotBlank
            @Size(min = 256, max = 256)
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

            @NotBlank
            @Size(max = 64)
            @Schema(description = "The operating system of the unit.", example = "Android")
            String os;

            @NotBlank
            @Size(min = 43, max = 128)
            @Schema(description = "PCKE-style code verifier", example = "M25iVXpKU3puUjFaYWg3T1NDTDQtcW1ROUY5YXlwalNoc0hhakxifmZHag")
            String code_verifier; // code_challenge_method=S256

        }

    }




}


/**
 * Request for updating password on a MinIdUser.
 * The new password must match the regexp. Length between 8 and 256, one or more numbers, one or more lowercase or character,
 * one or more uppercase character, special characters are allowed but optional.
 * <p>
 * Regexp explained:
 * <ul>
 * <li><code>(?=.*\p{Digit})</code>: At least one number.</li>
 * <li><code>(?=.*\p{Alpha})</code>: At least one lowercase or UPPERCASE character: a to Z.</li>
 * <li><code>(?=.{8,256})</code>: Length must be at least 8 characters and max 256 characters.</li>
 * <li><code>[\p{Alnum}!"#$%&'()*+,-./:;<=>?@\[\]\\^_`{}|~£€]+</code>: 1 or more numbers/characters/special characters.
 * Valid special special characters <code>[!"#$%&'()*+,-./:;<=>?@[]\^_`{}|~£€]</code></li>
 */


// public static final String PASSWORD_REGEXP = "^(?=.*\\p{Digit})(?=.*\\p{Alpha})(?=.{8,256})[\\p{Alnum}!\"#$%&'()*+,-./:;<=>?@\\[\\]\\\\^_`{}|~£€]+$";