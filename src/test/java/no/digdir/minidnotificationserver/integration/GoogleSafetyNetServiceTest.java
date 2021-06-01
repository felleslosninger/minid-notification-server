package no.digdir.minidnotificationserver.integration;

import com.google.firebase.messaging.FirebaseMessaging;
import no.digdir.minidnotificationserver.api.attestation.NonceEntity;
import no.digdir.minidnotificationserver.api.attestation.android.AttestationEntity;
import no.digdir.minidnotificationserver.domain.AttestationStatement;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseBeans;
import no.digdir.minidnotificationserver.service.GoogleSafetyNetService;
import no.digdir.minidnotificationserver.service.NotificationServerCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GoogleSafetyNetServiceTest {

    @MockBean
    private FirebaseBeans firebaseBeans;
    
    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @MockBean
    public JwtDecoder jwtDecoder;

    @Autowired
    private GoogleSafetyNetService googleSafetyNetService;

    @MockBean
    private NotificationServerCache cache;

    @Test
    public void verifyAttestationVerifiedTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();

        AttestationStatement attestationStatement =
                createAttestationStatement(true,true);

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertTrue(attestationResponseEntity.getVerified());
    }

    @Test
    public void verifyAttestationFailedNonceTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();
        nonceEntity.setNonce("should-not-match");

        AttestationStatement attestationStatement =
                createAttestationStatement(true,true);

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertFalse(attestationResponseEntity.getVerified());
    }

    @Test
    public void verifyAttestationFailedTimestampTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();

        AttestationStatement attestationStatement =
                createAttestationStatement(true,true);
        attestationStatement.setTimestampMs(new Date().getTime() - 86400000);

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertFalse(attestationResponseEntity.getVerified());
    }

    @Test
    public void verifyAttestationFailedPackageNameTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();

        AttestationStatement attestationStatement =
                createAttestationStatement(true,true);
        attestationStatement.setApkPackageName("should-not-match");

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertFalse(attestationResponseEntity.getVerified());
    }

    @Test
    public void verifyAttestationFailedBasicIntegrityTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();

        AttestationStatement attestationStatement =
                createAttestationStatement(false,true);

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertFalse(attestationResponseEntity.getVerified());
    }

    @Test
    public void verifyAttestationFailedCtsProfileMatchTest(){

        NonceEntity.Storage nonceEntity = createNonceStorageEntity();

        AttestationStatement attestationStatement =
                createAttestationStatement(true,false);

        AttestationEntity.Request attestationRequestEntity = createAttestationEntityRequest();

        Mockito.when(cache.getAttestationNonce(Mockito.any()))
                .thenReturn(nonceEntity);

        AttestationEntity.Response attestationResponseEntity =
                googleSafetyNetService.verifyAttestationStatement(attestationStatement,attestationRequestEntity);

        Assert.assertFalse(attestationResponseEntity.getVerified());
    }

    private NonceEntity.Storage createNonceStorageEntity(){
        return NonceEntity.Storage.builder().
                token("abcd1234").
                timestamp(new Date().getTime()).
                nonce("5af6d68588c56c21014746a427549b87f2369f19e442d9e526ae6547f9af618a").build();
    }

    private AttestationEntity.Request createAttestationEntityRequest() {
        return AttestationEntity.Request.builder().
                attestationJWT("something-random").
                token("abcd1234").
                build();
    }

    private AttestationStatement createAttestationStatement(
        Boolean requireBasicIntegrity, Boolean requireCtsProfileMatch){
        String nonce = "NWFmNmQ2ODU4OGM1NmMyMTAxNDc0NmE0Mjc1NDliODdmMjM2OWYxOWU0NDJkOWU1MjZhZTY1NDdmOWFmNjE4YQ==";
        long timestamp = new Date().getTime();
        return AttestationStatement.builder().
                timestampMs(timestamp).
                basicIntegrity(requireBasicIntegrity).
                ctsProfileMatch(requireCtsProfileMatch).
                apkPackageName("no.digdir.minid.authenticator").
                nonce(nonce).
                build();
    }

}
