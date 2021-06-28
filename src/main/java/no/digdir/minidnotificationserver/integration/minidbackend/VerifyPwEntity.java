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
        String locale;

        @Data
        @Builder
        static class ServiceProvider {
            String name;
            boolean dummy;
        }
    }

    @Data
    public static class Response {
        ResponseUser user;
        String requestUrn;
    }

    @Data
    public static class ResponseUser {
        private String pid;
        private String state;
        private String preferredLanguage;
        private String preferred2FaMethod;
        private String securityLevel;
        private Integer pinCodeIndex;
        private Integer pinCodeIndex2;
    }

}

