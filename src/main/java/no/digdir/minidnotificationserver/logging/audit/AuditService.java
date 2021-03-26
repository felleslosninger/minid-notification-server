package no.digdir.minidnotificationserver.logging.audit;



import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.onboarding.OnboardingStartRequestEntity;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.api.internal.validate.ValidateEntity;
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
                .personIdentifier(device.getPersonIdentifier())
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceUpdateDevice(RegistrationDevice existingDevice, RegistrationDevice updatedDevice) {

        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_UPDATE)
                .personIdentifier(existingDevice.getPersonIdentifier())
                .attribute("old_device", existingDevice)
                .attribute("new_device", updatedDevice)
                .build());
    }

    public void auditRegistrationServiceDeleteDevice(RegistrationDevice device) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_DELETE)
                .personIdentifier(device.getPersonIdentifier())
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceImportApnsToken(RegistrationEntity entity, String personIdentifier, String fcmToken) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APNS_TOKEN_IMPORT)
                .personIdentifier(personIdentifier)
                .attribute("apns_token", entity.getToken())
                .attribute("fcm_token", fcmToken)
                .build());
    }

    public void auditRegistrationServiceImportApnsToken(OnboardingStartRequestEntity entity, String personIdentifier, String fcmToken) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APNS_TOKEN_IMPORT)
                .personIdentifier(personIdentifier)
                .attribute("apns_token", entity.getToken())
                .attribute("fcm_token", fcmToken)
                .build());
    }
    public void auditOnboardingServiceImportApnsToken(OnboardingStartRequestEntity entity, String personIdentifier, String fcmToken) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APNS_TOKEN_IMPORT)
                .attribute("claimed_person_identifier", personIdentifier)
                .attribute("apns_token", entity.getToken())
                .attribute("fcm_token", fcmToken)
                .build());
    }


    public void auditNotificationSend(NotificationEntity notification, AdminContext adminContext) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.NOTIFICATION_SEND)
                .personIdentifier(adminContext.getPersonIdentifier())
                .attribute("admin_user_id", adminContext.getFullAdminUserId())
                .attribute("notification", notification)
                .build());
    }

    public void auditNotificationSend(NotificationEntity notification, String personIdentifier) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.NOTIFICATION_SEND)
                .personIdentifier(personIdentifier)
                .attribute("notification", notification)
                .build());
    }
    public void auditNotificationOnboardingSend(NotificationEntity notification, String personIdentifier) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.NOTIFICATION_ONBOARDING_SEND)
                .attribute("claimed_person_identifier", personIdentifier)
                .attribute("notification", notification)
                .build());
    }

    public void auditValidatePidToken(ValidateEntity validate, boolean result, AdminContext adminContext) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.VALIDATE_PID_TOKEN)
                .personIdentifier(adminContext.getPersonIdentifier())
                .attribute("admin_user_id", adminContext.getFullAdminUserId())
                .attribute("result", result)
                .attribute("validate", validate)
                .build());
    }


}
