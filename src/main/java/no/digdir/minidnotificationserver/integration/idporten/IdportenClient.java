package no.digdir.minidnotificationserver.integration.idporten;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
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

import static no.digdir.minidnotificationserver.config.filters.AdminContextFilter.ADMIN_USER_ID_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdportenClient {

    private final IdportenRestTemplate restTemplate;
    private final ConfigProvider configProvider;
    private String apiBaseUrl;
    private ConfigProvider.IdportenOidcProvider cfg;

    @PostConstruct
    public void init() {
        this.apiBaseUrl = configProvider.getIdportenOidcProvider().getUrl();
        this.cfg = configProvider.getIdportenOidcProvider();
    }


    public IdportenEntity.AuthorizeResponse backChannelAuthorize(String personIdentifier, String codeVerifier) {

        String codeChallenge = Utils.generateCodeChallange(codeVerifier);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", cfg.getClientId());
        map.add("redirect_uri", cfg.getRedirectUri());
        map.add("scope", Strings.join(cfg.getScopes(), ' '));
        map.add("login_hint", "pid:" + personIdentifier);
        map.add("code_challenge_method", "S256");
        map.add("code_challenge", codeChallenge);

        HttpEntity<MultiValueMap<String, String>> request = httpEntity(map, true);

        ResponseEntity<IdportenEntity.AuthorizeResponse> response = restTemplate.postForEntity(apiBaseUrl + "/admin/bc-authorize", request, IdportenEntity.AuthorizeResponse.class);
        return Objects.requireNonNull(response.getBody());
    }

    public IdportenEntity.TokenResponse token(String code, String codeVerifier) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", cfg.getClientId());
        map.add("grant_type", "authorization_code"); // c47143e4-2d1f-4b92-a9ba-2ae548c6a3bd
        map.add("redirect_uri", cfg.getRedirectUri());
        map.add("code", code);
        map.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request = httpEntity(map, false);

        ResponseEntity<IdportenEntity.TokenResponse> response = restTemplate.postForEntity(apiBaseUrl + "/token", request, IdportenEntity.TokenResponse.class);
        return Objects.requireNonNull(response.getBody());
    }


    public IdportenEntity.TokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = httpEntity(map, false);

        ResponseEntity<IdportenEntity.TokenResponse> response = restTemplate.postForEntity(apiBaseUrl + "/token", request, IdportenEntity.TokenResponse.class);
        return Objects.requireNonNull(response.getBody());
    }

    private <T> HttpEntity<T> httpEntity(T map, boolean withAuth) {
        return new HttpEntity<>(map, headers(withAuth));
    }

    private HttpHeaders headers(boolean withAuth) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(ADMIN_USER_ID_HEADER, "minid-notification-server");
        if(withAuth) {
            String auth = cfg.getOidcClientId() + ":" + cfg.getOidcClientSecret();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII) );
            String authHeader = "Basic " + new String( encodedAuth );
            headers.set( "Authorization", authHeader );
        }
        return headers;
    }

}
