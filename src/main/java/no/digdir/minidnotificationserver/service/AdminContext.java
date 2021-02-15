package no.digdir.minidnotificationserver.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

@Data
@Builder
public class AdminContext {

    public static final String ADMIN_USER_ID_HEADER = "admin-user-id";

    private String adminUserId;
    private OAuth2AuthenticatedPrincipal principal;

    public static AdminContext of(HttpHeaders headers, OAuth2AuthenticatedPrincipal principal) {
        return AdminContext.builder()
                .adminUserId(headers.getFirst(ADMIN_USER_ID_HEADER))
                .principal(principal)
                .build();
    }

    public String getFullAdminUserId() {
        return String.format("%s-%s", getPrincipal().getAttribute("pid"), getAdminUserId());
    }

    public String getPersonIdentifier() {
        return getPrincipal().getAttribute("pid");
    }


}
