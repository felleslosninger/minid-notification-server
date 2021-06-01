package no.digdir.minidnotificationserver.api.attestation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.NotificationServerCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attestation")
@RequiredArgsConstructor
public class NonceEndpoint {

    private final NotificationServerCache cache;

    @Operation(summary = "Returns unique nonce value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nonce generated")
    })
    @PostMapping("/nonce")
    public ResponseEntity<NonceEntity.Response> verifyAttestation(@RequestBody NonceEntity.Request nonceRequestEntity) {
        NonceEntity.Storage nonceStorageEntity =
                new NonceEntity.Storage().fromRequestEntity(nonceRequestEntity);

        cache.putAttestationNonce(nonceRequestEntity.getToken(), nonceStorageEntity);

        return new ResponseEntity<>(nonceStorageEntity.getNonceResponse(), HttpStatus.OK);
    }

}
