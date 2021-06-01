package no.digdir.minidnotificationserver.api.attestation.android;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.aspect.attestation.EnforceAttestation;
import no.digdir.minidnotificationserver.service.GoogleSafetyNetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attestation/android")
@RequiredArgsConstructor
public class AttestationEndpoint {

    private final GoogleSafetyNetService googleSafetyNetService;

    @Operation(summary = "Verify android device attestation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verified")
    })
    @PostMapping("/verify")
    public ResponseEntity<AttestationEntity.Response> verifyAttestation(@RequestBody AttestationEntity.Request attestationEntity){
        AttestationEntity.Response responseEntity = googleSafetyNetService.verifyAttestation(attestationEntity);
        return ResponseEntity.ok(responseEntity);
    }

    @Operation(summary = "Test attestation enforcement during development")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verified")
    })
    @PostMapping("/test")
    @EnforceAttestation
    public ResponseEntity<String> testAttestation(@RequestBody DeviceEntity entity){
        return ResponseEntity.ok().build();
    }

}
