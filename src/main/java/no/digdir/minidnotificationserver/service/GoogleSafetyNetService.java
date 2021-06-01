package no.digdir.minidnotificationserver.service;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.attestation.NonceEntity;
import no.digdir.minidnotificationserver.api.attestation.android.AttestationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.domain.AttestationStatement;
import no.digdir.minidnotificationserver.exceptions.AttestationProblem;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleSafetyNetService {

    private static final DefaultHostnameVerifier HOSTNAME_VERIFIER = new DefaultHostnameVerifier();

    private final NotificationServerCache cache;
    private final ConfigProvider configProvider;

    private Integer NONCE_TIMEOUT;
    private Integer ATTESTATION_TIMEOUT;
    private Boolean REQUIRE_CTS_PROFILE_MATCH;
    private Boolean REQUIRE_BASIC_INTEGRITY;

    @PostConstruct
    public void init() {
        this.ATTESTATION_TIMEOUT = configProvider.getAttestation().getAttestationTimeout();
        this.NONCE_TIMEOUT = configProvider.getAttestation().getNonceTimeout();
        this.REQUIRE_BASIC_INTEGRITY = configProvider.getAttestation().getRequireBasicIntegrity();
        this.REQUIRE_CTS_PROFILE_MATCH = configProvider.getAttestation().getRequireCtsProfileMatch();
    }

    @Audit(auditId = AuditID.ATTESTATION_VERIFY)
    public AttestationEntity.Response verifyAttestation(AttestationEntity.Request attestationEntity) {
        AttestationStatement stmt = parseAndVerify(attestationEntity.getAttestationJWT());
        if (stmt == null) {
            log.error("Failure: Failed to parse and verify the attestation statement.");
            return attestationIsVerified(false);
        }

        return verifyAttestationStatement(stmt, attestationEntity);
    }

    private AttestationStatement parseAndVerify(String signedAttestationStatment) {
        // Parse JSON Web Signature format.
        JsonWebSignature jws;
        try {
            jws = JsonWebSignature.parser(JacksonFactory.getDefaultInstance())
                    .setPayloadClass(AttestationStatement.class).parse(signedAttestationStatment);
        } catch (IOException e) {
            throw new AttestationProblem("Failure: " + signedAttestationStatment + " is not valid JWS " + "format.");
        } catch (Exception e) {
            throw new AttestationProblem("Failure: Failed to parse JWS (" + e.getMessage() + ")");
        }

        // Verify the signature of the JWS and retrieve the signature certificate.
        X509Certificate cert;
        try {
            cert = jws.verifySignature();
            if (cert == null) {
                throw new AttestationProblem("Failure: Signature verification failed.");
            }
        } catch (GeneralSecurityException e) {
            throw new AttestationProblem("Failure: Error during cryptographic verification of the JWS signature.");
        }

        // Verify the hostname of the certificate.
        if (!verifyHostname("attest.android.com", cert)) {
            throw new AttestationProblem("Failure: Certificate isn't issued for the hostname attest.android.com.");
        }

        // Extract and use the payload data.
        return (AttestationStatement) jws.getPayload();
    }

    /**
     * Verifies that the certificate matches the specified hostname.
     * Uses the {@link DefaultHostnameVerifier} from the Apache HttpClient library
     * to confirm that the hostname matches the certificate.
     *
     * @param hostname
     * @param leafCert
     * @return
     */
    private boolean verifyHostname(String hostname, X509Certificate leafCert) {
        try {
            // Check that the hostname matches the certificate. This method throws an exception if
            // the cert could not be verified.
            HOSTNAME_VERIFIER.verify(hostname, leafCert);
            return true;
        } catch (SSLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public AttestationEntity.Response attestationIsVerified(Boolean verified) {
        AttestationEntity.Response responseEntity = new AttestationEntity.Response();
        responseEntity.setVerified(verified);
        return responseEntity;
    }


    public AttestationEntity.Response verifyAttestationStatement(AttestationStatement stmt, AttestationEntity.Request attestationEntity){
         /*
            Check nonce
         */
        NonceEntity.Storage cachedNonceEntity =
                cache.getAttestationNonce(attestationEntity.getToken());

        if (cachedNonceEntity == null) {
            log.warn("Failure: Failed to retrieve cached nonce from token");
            return attestationIsVerified(false);
        }

        String attestationNonce = new String(stmt.getNonce());
        if (!cachedNonceEntity.getNonce().equals(attestationNonce)) {
            log.warn("Failure: Server side nonce did not match request nonce");
            return attestationIsVerified(false);
        }

        /*
            Check timestamp
        */
        long nonceCreated = cachedNonceEntity.getTimestamp();
        long attestationCreated = stmt.getTimestampMs();

        if( timestampTimeout(nonceCreated, NONCE_TIMEOUT) || timestampTimeout(attestationCreated, ATTESTATION_TIMEOUT)){
            log.warn("Failure: Attestation timestamp exceeds timeout value");
            return attestationIsVerified(false);
        }

        /*
            Check packageName
         */
        if(stmt.getApkPackageName() == null){
            log.warn("Failure: No APK package name provided");
            return attestationIsVerified(false);
        }

        if(!stmt.getApkPackageName().equals("no.digdir.minid.authenticator")){
            log.warn("Failure: Package name does not match");
            return attestationIsVerified(false);
        }

        /*
            Check CTS profile and Basic integrity match
         */
        if(REQUIRE_CTS_PROFILE_MATCH){
            if(stmt.isCtsProfileMatch() != REQUIRE_CTS_PROFILE_MATCH){
                log.warn("Failure: CtsProfileMatch is: " + stmt.isCtsProfileMatch() + " when it should be: " + REQUIRE_CTS_PROFILE_MATCH);
                return attestationIsVerified(false);
            }
        }

        if(REQUIRE_BASIC_INTEGRITY){
            if(stmt.hasBasicIntegrity() != REQUIRE_BASIC_INTEGRITY){
                log.warn("Failure: BasicIntegrity is: " + stmt.hasBasicIntegrity() + " when it should be: " + REQUIRE_BASIC_INTEGRITY);
                return attestationIsVerified(false);
            }
        }

        return attestationIsVerified(true);
    }

    private boolean timestampTimeout(long timestampToCheck, int timeoutValue) {
        long lastValidTimestamp = new Date().getTime() - timeoutValue;
        return timestampToCheck < lastValidTimestamp;
    }

}
