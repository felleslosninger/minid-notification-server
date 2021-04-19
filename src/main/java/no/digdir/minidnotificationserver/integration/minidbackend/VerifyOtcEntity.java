package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOtcEntity {

    @Data
    @Builder
    static class Request {
        String pid;
        String otc;
        String requestUrn; // nonce
    }

}

