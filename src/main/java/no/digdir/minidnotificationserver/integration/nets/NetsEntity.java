package no.digdir.minidnotificationserver.integration.nets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetsEntity {

    @Data
    public static class CibaSessionResponse {
       private String auth_req_id;
       private Long interval;
       private Long expires_in;
    }

    @Data
    public static class TokenResponse {
       private String id_token;
    }

}
