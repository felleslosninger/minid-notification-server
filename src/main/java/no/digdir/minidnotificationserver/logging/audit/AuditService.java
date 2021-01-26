package no.digdir.minidnotificationserver.logging.audit;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.service.AdminContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class AuditService {

    private final Logger auditLogger = LoggerFactory.getLogger("no.digdir.logging.AuditLog");
    private final Logger logger = LoggerFactory.getLogger(AuditService.class);

    public AuditService(@Autowired ConfigProvider configProvider) {

        System.getProperties().setProperty("AUDIT_LOGDIR", configProvider.getAudit().getLogDir());
        System.getProperties().setProperty("AUDIT_LOGFILE", configProvider.getAudit().getLogFile());

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            InputStream auditXml = new ClassPathResource("logback-audit.xml").getInputStream();
            configurator.doConfigure(auditXml);
            logger.info("Audit logging configured.");
        } catch (JoranException | IOException je) {
            logger.info("Issues occurred during configuration of audit logging.");
            // StatusPrinter will print more information
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public void auditRegistrationServiceRegisterDevice(RegistrationDevice device) {
        auditLogger.info(
                "Device registered.",
                kv("audit_id", AuditID.DEVICE_REGISTER.auditId()),
                kv("person_identifier", device.getPersonIdentifier()),
                kv("device", device)
        );
    }

    public void auditRegistrationServiceUpdateDevice(RegistrationDevice existingDevice, RegistrationDevice updatedDevice) {
        auditLogger.info(
                "Device updated.",
                kv("audit_id", AuditID.DEVICE_UPDATE.auditId()),
                kv("person_identifier", existingDevice.getPersonIdentifier()),
                kv("old_device", existingDevice),
                kv("new_device", updatedDevice)
        );
    }

    public void auditRegistrationServiceDeleteDevice(RegistrationDevice device) {
        auditLogger.info(
                "Device deleted.",
                kv("audit_id", AuditID.DEVICE_DELETE.auditId()),
                kv("person_identifier", device.getPersonIdentifier()),
                kv("device", device)
        );
    }


    public void auditNotificationSend(NotificationEntity notification, AdminContext adminContext) {
        auditLogger.info(
                "Notification sent.",
                kv("audit_id", AuditID.NOTIFICATION_SEND.auditId()),
                kv("admin_user_id", adminContext.getFullAdminUserId()),
                kv("person_identifier", adminContext.getPersonIdentifier()),
                kv("notification", notification)
        );
    }
}
