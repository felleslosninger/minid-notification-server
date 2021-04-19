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
        String pincodeIndex;
        String pincode;
        String requestUrn; // nonce
    }

}

