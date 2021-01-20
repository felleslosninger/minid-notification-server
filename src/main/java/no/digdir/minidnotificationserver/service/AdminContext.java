package no.digdir.minidnotificationserver.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;

@Data
@Builder
public class AdminContext {

    public static final String ADMIN_USER_ID_HEADER = "admin-user-id";

    private String adminUserId;
    private Jwt accessToken;

    public static AdminContext of(HttpHeaders headers, Jwt jwt) {
        return AdminContext.builder()
                .adminUserId(headers.getFirst(ADMIN_USER_ID_HEADER))
                .accessToken(jwt)
                .build();
    }

    public String getFullAdminUserId() {
        return String.format("%s-%s", getAccessToken().getClaimAsString("pid"), getAdminUserId());
    }

    public String getPersonIdentifier() {
        return getAccessToken().getClaimAsString("pid");
    }


}
