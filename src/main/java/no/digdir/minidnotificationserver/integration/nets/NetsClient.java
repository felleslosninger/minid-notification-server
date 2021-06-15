package no.digdir.minidnotificationserver.integration.nets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetsClient {
    private final NetsRestTemplate restTemplate;
    private final ConfigProvider configProvider;
    private String apiBaseUrl;
    private ConfigProvider.NetsOidcProvider cfg;

    @PostConstruct
    public void init() {
        this.apiBaseUrl = configProvider.getNetsOidcProvider().getUrl();
        this.cfg = configProvider.getNetsOidcProvider();
    }

    @Audit(auditId = AuditID.NETS_CIBA_SESSION)
    public NetsEntity.CibaSessionResponse session() {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("scope", "openid profile ssn");
        map.add("backchannel_token_delivery_mode", "poll");
        map.add("amr_values", "passport_reader");

        HttpEntity<MultiValueMap<String, String>> request = httpEntity(map);

        ResponseEntity<NetsEntity.CibaSessionResponse> response = restTemplate.postForEntity(apiBaseUrl + "/ciba", request, NetsEntity.CibaSessionResponse.class);
        return Objects.requireNonNull(response.getBody());
    }

    @Audit(auditId = AuditID.NETS_CIBA_TOKEN)
    public NetsEntity.TokenResponse token(String authRequestId) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "urn:openid:params:grant-type:ciba");
        map.add("auth_req_id", authRequestId);

        HttpEntity<MultiValueMap<String, String>> request = httpEntity(map);

        ResponseEntity<NetsEntity.TokenResponse> response = restTemplate.postForEntity(apiBaseUrl + "/token", request, NetsEntity.TokenResponse.class);
        return Objects.requireNonNull(response.getBody());

    }

    private <T> HttpEntity<T> httpEntity(T map) {
        return new HttpEntity<>(map, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String auth = cfg.getClientId() + ":" + cfg.getClientSecret();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII) );
        String authHeader = "Basic " + new String( encodedAuth );
        headers.set( "Authorization", authHeader );
        return headers;
    }
}


/*
{
    "error_description": "Consent confirmation still pending",
    "error": "authorization_pending"
}
 */


/*
payload: {
  "sub": "passport_reader:CCC026592",
  "interpreted_nationality": "Norwegian",
  "birthdate": "24.02.1976",
  "gender": "MALE",
  "facematch_level": "7",
  "amr": "passport_reader",
  "iss": "https://www.ident-preprod1.nets.eu/oidc",
  "pid": "CCC026592",
  "issuing_country": "NOR",
  "auth_time": "14-06-2021 21:45:53 UTC",
  "exp": 1623708055,
  "iat": 1623707155,
  "jti": "0a5a062616494fe8bbc93b917c8a8a04",
  "document_type": "P",
  "document_number": "CCC026592",
  "place_of_birth": "SWE",
  "aud": "DIHJKHGSAZGL",
  "nationality": "NOR",
  "secondary_identifier": "KIM PETER MIKAEL",
  "date_of_expiry": "06.11.2030",
  "name": "TAAVO, KIM PETER MIKAEL",
  "personal_number": "24027625599",
  "interpreted_issuing_country": "Norway",
  "primary_identifier": "TAAVO",
  "auth_files_url": "https://www.ident-preprod1.nets.eu/auth_files/fetch.html?tid=0a5a062616494fe8bbc93b917c8a8a04"
}
 */