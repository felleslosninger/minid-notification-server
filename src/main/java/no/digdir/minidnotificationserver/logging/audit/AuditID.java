package no.digdir.minidnotificationserver.logging.audit;

public enum AuditID {

    DEVICE_REGISTER(1, "DEVICE-REGISTER"),
    DEVICE_UPDATE(2, "DEVICE-UPDATE"),
    DEVICE_DELETE(3, "DEVICE-DELETE"),

    NOTIFICATION_SEND(10, "NOTIFICATION-SEND");

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
