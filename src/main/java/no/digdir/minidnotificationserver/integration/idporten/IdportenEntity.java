package no.digdir.minidnotificationserver.integration.idporten;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdportenEntity {

    @Data
    public static class AuthorizeResponse {
        private String authorization_code;
    }

    @Data
    public static class TokenResponse {
        private String access_token;
        private String token_type;
        private Long expires_in;
        private String refresh_token;
        private String scope;
    }

}
