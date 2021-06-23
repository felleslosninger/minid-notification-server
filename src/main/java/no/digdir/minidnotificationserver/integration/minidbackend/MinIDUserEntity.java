package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.idporten.domain.user.Pincodes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinIDUserEntity {

    @Data
    public static class Response { // MinID User
        private String pid;
        private LocalDate birthDate;
        private String phoneNumber;
        private String email;
        private String state; // NORMAL, NEW_USER, LOCKED, TEMP_PWD, QUARANTINED, CLOSED, QUARANTINED_NEW_USER
        private LocalDateTime consentDate;
        private String preferredLanguage;
        private String preferred2FaMethod; // app, otc, pin
        private String dummyChannel;
        private String securityLevel;
        private Integer pinCodeIndex;
        private Integer pinCodeIndex2;
        private String source;
        private LocalDateTime tipCheckDate;
        private boolean minIdOnTheFlyUser;
        private String uid;

        private Pincodes pincodes; // from EnrichedUser
    }
}
