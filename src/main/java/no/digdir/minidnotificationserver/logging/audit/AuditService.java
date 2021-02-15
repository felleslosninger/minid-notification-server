package no.digdir.minidnotificationserver.logging.audit;



import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.api.validate.ValidateEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.service.AdminContext;

import no.idporten.logging.audit.AuditConfig;
import no.idporten.logging.audit.AuditEntry;
import no.idporten.logging.audit.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    AuditLogger auditLogger;

    public AuditService(@Autowired ConfigProvider configProvider) {
        auditLogger = new AuditLogger(AuditConfig.builder()
                .applicationName("no.digdir:minid-notification-server")
                .logfileDir(configProvider.getAudit().getLogDir())
                .logfileName(configProvider.getAudit().getLogFile())
                .build());
    }

    public void auditRegistrationServiceRegisterDevice(RegistrationDevice device) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_REGISTER)
                .message("")
                .attribute("person_identifier", device.getPersonIdentifier())
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceUpdateDevice(RegistrationDevice existingDevice, RegistrationDevice updatedDevice) {

        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_UPDATE)
                .message("")
                .attribute("person_identifier", existingDevice.getPersonIdentifier())
                .attribute("old_device", existingDevice)
                .attribute("new_device", updatedDevice)
                .build());
    }

    public void auditRegistrationServiceDeleteDevice(RegistrationDevice device) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_DELETE)
                .message("")
                .attribute("person_identifier", device.getPersonIdentifier())
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceImportApnsToken(RegistrationEntity entity, String personIdentifier, String fcmToken) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APNS_TOKEN_IMPORT)
                .message("")
                .attribute("person_identifier", personIdentifier)
                .attribute("apns_token", entity.getToken())
                .attribute("fcm_token", fcmToken)
                .build());
    }


    public void auditNotificationSend(NotificationEntity notification, AdminContext adminContext) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.NOTIFICATION_SEND)
                .message("")
                .attribute("admin_user_id", adminContext.getFullAdminUserId())
                .attribute("person_identifier", adminContext.getPersonIdentifier())
                .attribute("notification", notification)
                .build());
    }

    public void auditValidatePidToken(ValidateEntity validate, boolean result, AdminContext adminContext) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.VALIDATE_PID_TOKEN)
                .message("")
                .attribute("admin_user_id", adminContext.getFullAdminUserId())
                .attribute("person_identifier", adminContext.getPersonIdentifier())
                .attribute("result", result)
                .attribute("validate", validate)
                .build());
    }


}
