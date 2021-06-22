package no.digdir.minidnotificationserver.logging.audit;

import no.idporten.logging.audit.AuditIdentifier;

public enum AuditID implements AuditIdentifier {

    DEVICE_REGISTER(1, "DEVICE-REGISTER"),
    DEVICE_UPDATE(2, "DEVICE-UPDATE"),
    DEVICE_DELETE(3, "DEVICE-DELETE"),
    DEVICE_CREATE(4, "DEVICE-CREATE"),

    APNS_TOKEN_IMPORT(7, "APNS-TOKEN-IMPORT"),

    VALIDATE_PID_TOKEN(8, "VALIDATE-PID-TOKEN"),

    NOTIFICATION_SERVICE(10, "NOTIFICATION-SERVICE"),
    NOTIFICATION_SEND(11, "NOTIFICATION-SEND"),

    ONBOARDING_START(12, "ONBOARDING-START"),
    ONBOARDING_CONTINUE(13, "ONBOARDING-CONTINUE"),
    ONBOARDING_FINALIZE(14, "ONBOARDING-FINALIZE"),

    APP_AUTHORIZE(15, "APP-AUTHORIZE"),
    REQUEST_AUTHORIZATION(16, "REQUEST-AUTHORIZATION"),
    BACKCHANNEL_AUTHORIZE(17, "BACKCHANNEL-AUTHORIZE"),

    ATTESTATION_NONCE(18, "ATTESTATION-NONCE"),
    ATTESTATION_VERIFY(19, "ATTESTATION-VERIFY"),

    NETS_CIBA_SESSION(20, "NETS-CIBA-SESSION"),
    NETS_CIBA_TOKEN(21, "NETS-CIBA-TOKEN"),

    PASSPORT_ONBOARDING_START(22, "PASSPORT-ONBOARDING-START"),
    PASSPORT_ONBOARDING_FINALIZE(23, "PASSPORT-ONBOARDING-FINALIZE")

            ;

    static final String AUDIT_ID_FORMAT = "MINID-NOTIFICATION-%d-%s";
    final String auditId;
    final int id;

    AuditID(int numericId, String stringId) {
        this.auditId = String.format(AUDIT_ID_FORMAT, numericId, stringId);
        this.id = numericId;
    }

    public int id() {
        return id;
    }

    public String auditId() {
        return auditId;
    }

}
