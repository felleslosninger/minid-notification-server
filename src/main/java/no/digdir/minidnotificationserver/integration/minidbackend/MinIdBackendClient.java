package no.digdir.minidnotificationserver.integration.minidbackend;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinIdBackendClient {
    private final ConfigProvider configProvider;
    private final MinIdBackendRestTemplate restTemplate;
    private String apiBaseUrl;
    private static final String ADMIN_USER_ID_HEADER = "admin-user-id";

    @PostConstruct
    public void init() {
        this.apiBaseUrl = configProvider.getMinidBackendService().getUrl() + "/api";
    }

    public MinIDUserEntity.Response getUser(String pid) { // TODO: pid is not sanitized - direct user input!!!
        ResponseEntity<MinIDUserEntity.Response> response = restTemplate.getForEntity(apiBaseUrl + "/users/{pid}?enrich=true", MinIDUserEntity.Response.class, pid);
        return response.getBody();
    }

    public VerifyPwEntity.Response verifyPassword(String pid, String password, String serviceProvider) {
        VerifyPwEntity.Request requestEntity = VerifyPwEntity.Request.builder()
                .pid(pid)
                .password(password)
                .serviceProvider(VerifyPwEntity.Request.ServiceProvider.builder().name(serviceProvider).build())
                .locale("nb")
                .build();
        ResponseEntity<VerifyPwEntity.Response> response = restTemplate.postForEntity(apiBaseUrl + "/auth/verify_pw_2fa", httpEntity(requestEntity), VerifyPwEntity.Response.class);
        return response.getBody();
    }

    public void verifyOtc2Fa(String pid, String otc, String requestUrn) {
        VerifyOtcEntity.Request requestEntity = VerifyOtcEntity.Request.builder()
                .pid(pid)
                .otc(otc)
                .requestUrn(requestUrn)
                .build();
        restTemplate.postForEntity(apiBaseUrl + "/auth/verify_otc_2fa", httpEntity(requestEntity), String.class);
    }


    public void verifyPin2Fa(String pid, String pincode, Integer pincodeIndex, String requestUrn) {
        VerifyPinEntity.Request requestEntity = VerifyPinEntity.Request.builder()
                .pid(pid)
                .pincode(pincode)
                .pincodeIndex(pincodeIndex.toString())
                .requestUrn(requestUrn)
                .build();
        restTemplate.postForEntity(apiBaseUrl + "/auth/verify_pin_2fa", httpEntity(requestEntity), String.class);
    }

    public void verifyPin(String pid, String pincode, Integer pincodeIndex, String pincode2, Integer pincodeIndex2) {
        VerifyPinEntity.Request requestEntity = VerifyPinEntity.Request.builder()
                .pid(pid)
                .pincode(pincode)
                .pincodeIndex(pincodeIndex.toString())
                .pincode2(pincode2)
                .pincodeIndex2(pincodeIndex2.toString())
                .build();
        restTemplate.postForEntity(apiBaseUrl + "/auth/verify_pin", httpEntity(requestEntity), String.class);
    }

    public void setPreferredTwoFactorMethod(String personIdentifier, String twoFactorMethod) {
        Preferred2FaMethodEntity.Request requestEntity = Preferred2FaMethodEntity.Request.builder().preferred2FaMethod(twoFactorMethod).build();
        restTemplate.patchForObject(apiBaseUrl + "/users/" + personIdentifier, httpEntity(requestEntity), String.class);
    }

    public void setPassword(String personIdentifier, String password) {
        MinIdPasswordEntity.Request requestEntity = MinIdPasswordEntity.Request.builder()
                .pid(personIdentifier)
                .password(password)
                .build();
        restTemplate.patchForObject(apiBaseUrl + "/auth/update_pw", httpEntity(requestEntity), String.class);
    }

    private <T> HttpEntity<T> httpEntity(T object) {
        return new HttpEntity<>(object, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(ADMIN_USER_ID_HEADER, "minid-notification-server");
        return headers;
    }

}
