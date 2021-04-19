package no.digdir.minidnotificationserver.integration.minidauthentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import no.digdir.minidnotificationserver.service.AuthorizationService;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

import javax.annotation.PostConstruct;

import static no.digdir.minidnotificationserver.config.AdminContextFilter.ADMIN_USER_ID_HEADER;
import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinIdAuthenticationClient {
    private final MinIdAuthenticationRestTemplate restTemplate;
    private final ConfigProvider configProvider;
    private String apiBaseUrl;

    @PostConstruct
    public void init() {
        this.apiBaseUrl = configProvider.getMinidAuthenticationService().getUrl();
    }

    public boolean exchange(String personIdentifier, String loginAttemptId, AuthorizationService.AuthAction action) {
        String url = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl + "/" + action)
                .queryParam("login_attempt_id", loginAttemptId)
                .queryParam("person_identifier", personIdentifier)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity(),
                String.class);

        if(!response.getStatusCode().is2xxSuccessful()) {
            ProblemBuilder builder = Problem.builder()
                    .withType(ErrorConstants.MINID_AUTH_TYPE)
                    .withStatus(BAD_REQUEST)
                    .with("correlation_id", MDC.get(CORRELATION_ID_HEADER))
                    .withTitle("Issue with MinID Authentication Service endpoint.")
                    .withDetail(response.getStatusCode().toString())
                    ;
            throw builder.build();
        }
        return true;
    }


    private <T> HttpEntity<T> httpEntity() {
        return new HttpEntity<>(headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(ADMIN_USER_ID_HEADER, "minid-notification-server");
        return headers;
    }


}
