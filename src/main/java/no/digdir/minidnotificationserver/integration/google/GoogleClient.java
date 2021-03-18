package no.digdir.minidnotificationserver.integration.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient {
    // https://developers.google.com/instance-id/reference/server#create_registration_tokens_for_apns_tokens
    private final ConfigProvider configProvider;
    private final GoogleRestTemplate restTemplate;

    private static final String IID_API_URI = "https://iid.googleapis.com/iid/v1:batchImport";

    public String importAPNsToken(String apnsToken) {

        GoogleRequestEntity requestEntity = GoogleRequestEntity.builder()
                .apns_tokens(new HashSet<>(Arrays.asList(apnsToken)))
                .application(configProvider.getGoogleApi().getBundleId())
                .sandbox(configProvider.getGoogleApi().isSandbox())
                .build();

        ResponseEntity<GoogleResponseEntity> response = restTemplate.postForEntity(IID_API_URI, httpEntity(requestEntity), GoogleResponseEntity.class);

        if(HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null) {
            GoogleResponseEntity.ListItem responseItem = response.getBody().getResults().stream()
                    .filter(li -> li.getApns_token().equals(apnsToken))
                    .findFirst()
                    .orElse(null);

            if(responseItem != null && "OK".equalsIgnoreCase(responseItem.getStatus())) {
                return responseItem.getRegistration_token();
            }
        }

        String errorMessage = "Error occurred during registration of APNs token: " + response.getStatusCode() + " - " + response.getBody();
        log.error(errorMessage);
        throw new GoogleProblem(errorMessage);

    }

    private <T> HttpEntity<T> httpEntity(T object) {
        return new HttpEntity(object, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "key=" + configProvider.getGoogleApi().getAuthKey());
        return headers;
    }

}