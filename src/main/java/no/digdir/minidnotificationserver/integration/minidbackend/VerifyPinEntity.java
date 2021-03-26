package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyPinEntity {
    @Data
    @Builder
    static class Request {
        String pid;
        String pincodeindex;
        String pincode;
        String requestUrn; // nonce
    }

    @Data
    public static class Response {
        boolean pinCodeVerified;
        boolean lastTry;
    }

}

