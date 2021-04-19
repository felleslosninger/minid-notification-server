package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.integration.idporten.IdportenClient;
import no.digdir.minidnotificationserver.integration.idporten.IdportenEntity;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.utils.Utils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdportenService {

    private final IdportenClient idportenClient;

    @Audit(auditId = AuditID.BACKCHANNEL_AUTHORIZE)
    public IdportenEntity.TokenResponse backchannelAuthorize(String personIdentifier) {
        String codeVerifier = Utils.generateCodeVerifier();
        IdportenEntity.AuthorizeResponse authorizeResponse = idportenClient.backChannelAuthorize(personIdentifier, codeVerifier);
        IdportenEntity.TokenResponse tokenResponse = idportenClient.token(authorizeResponse.getAuthorization_code(), codeVerifier);

        return tokenResponse;
    }

}
