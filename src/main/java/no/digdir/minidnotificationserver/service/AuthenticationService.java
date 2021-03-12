package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.integration.google.GoogleResponseEntity;
import no.digdir.minidnotificationserver.integration.minidEid.ApiRestTemplate;
import no.digdir.minidnotificationserver.integration.minidEid.MinIDEidApprovalRequestEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * class for calling minid-authentication with approve/reject
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final ConfigProvider configProvider;
    private final ApiRestTemplate restTemplate;

    public ResponseEntity sendApproval(String ssn) {
        MinIDEidApprovalRequestEntity requestEntity = MinIDEidApprovalRequestEntity.builder().personal_identity(ssn).build();
        return restTemplate.postForEntity(configProvider.getMinIDEid().getUrl() + "/approve"
                , httpEntity(requestEntity), GoogleResponseEntity.class);
    }

    public ResponseEntity sendRejection(String ssn) {
        MinIDEidApprovalRequestEntity requestEntity = MinIDEidApprovalRequestEntity.builder().personal_identity(ssn).build();
        return restTemplate.postForEntity(configProvider.getMinIDEid().getUrl() + "/reject"
                , httpEntity(requestEntity), GoogleResponseEntity.class);
    }

    private <T> HttpEntity<T> httpEntity(T object) {
        return new HttpEntity(object, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
