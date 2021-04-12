package no.digdir.minidnotificationserver.logging.audit;


import no.digdir.minidnotificationserver.api.authorization.AuthorizationEntity;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.api.internal.validate.ValidateEntity;
import no.digdir.minidnotificationserver.api.onboarding.OnboardingEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.domain.Device;
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

    public void auditRegistrationServiceRegisterDevice(Device device) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_REGISTER)
                .personIdentifier(device.getPersonIdentifier())
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceUpdateDevice(Device existingDevice, Device updatedDevice) {

        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_UPDATE)
                .personIdentifier(existingDevice.getPersonIdentifier())
                .attribute("old_device", existingDevice)
                .attribute("new_device", updatedDevice)
                .build());
    }

    public void auditRegistrationServiceDeleteDevice(String personIdentifier, Device device) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.DEVICE_DELETE)
                .personIdentifier(personIdentifier)
                .attribute("device", device)
                .build());
    }

    public void auditRegistrationServiceImportApnsToken(DeviceEntity entity, String personIdentifier, String fcmToken) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APNS_TOKEN_IMPORT)
                .personIdentifier(personIdentifier)
                .attribute("apns_token", entity.getToken())
                .attribute("fcm_token", fcmToken)
                .build());
    }

    public void auditOnboardingServiceImportApnsToken(OnboardingEntity.Start.Request entity, String personIdentifier, String fcmToken) {
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

    public void auditNotificationOnboardingSend(NotificationEntity notification, String personIdentifier) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.NOTIFICATION_ONBOARDING_SEND)
                .attribute("claimed_person_identifier", personIdentifier)
                .attribute("notification", notification)
                .build());
    }

    public void auditOnboardingStart(OnboardingEntity.Start.Request entity) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.ONBOARDING_START)
                .attribute("claimed_person_identifier", entity.getPerson_identifier())
                .attribute("entity", entity)
                .build());
    }

    public void auditOnboardingContinue(OnboardingEntity.Continue.Request entity, String personIdentifier) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.ONBOARDING_CONTINUE)
                .attribute("claimed_person_identifier", personIdentifier)
                .attribute("entity", entity)
                .build());
    }

    public void auditOnboardingFinalize(OnboardingEntity.Finalize.Request entity) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.ONBOARDING_FINALIZE)
                .attribute("claimed_person_identifier", entity.getPerson_identifier())
                .attribute("entity", entity)
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

    public void auditAppAuthorize(String personIdentifier, AuthorizationEntity entity, String action) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.APP_AUTHORIZE)
                .personIdentifier(personIdentifier)
                .attribute("action", action)
                .attribute("entity", entity)
                .build());
    }

    public void auditAuthorizationRequest(RequestAuthorizationEntity entity, AdminContext adminContext) {
        auditLogger.log(AuditEntry.builder()
                .auditId(AuditID.REQUEST_AUTHORIZATION)
                .personIdentifier(adminContext.getPersonIdentifier())
                .attribute("admin_user_id", adminContext.getFullAdminUserId())
                .attribute("entity", entity)
                .build());
    }

}
