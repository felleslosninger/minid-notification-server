package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyPwEntity {

    @Data
    @Builder
    static class Request {
        String pid;
        String password;
        ServiceProvider serviceProvider;

        @Data
        @Builder
        static class ServiceProvider {
            String name;
            boolean dummy;
        }
    }

    @Data
    public static class Response {
        String minIdUserState; // NORMAL, NEW_USER, LOCKED, TEMP_PWD, QUARANTINED, CLOSED, QUARANTINED_NEW_USER
        String preferred2FaMethod; // app, otc, pin
        Integer pinCodeIndex;
        String requestUrn; // nonce
    }

}

