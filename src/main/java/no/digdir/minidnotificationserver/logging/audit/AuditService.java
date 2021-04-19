package no.digdir.minidnotificationserver.logging.audit;


import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.domain.Device;
import no.idporten.logging.audit.AuditConfig;
import no.idporten.logging.audit.AuditEntry;
import no.idporten.logging.audit.AuditLogger;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Aspect
public class AuditService {

    AuditLogger auditLogger;

    public AuditService(@Autowired ConfigProvider configProvider) {
        auditLogger = new AuditLogger(AuditConfig.builder()
                .applicationName("no.digdir:minid-notification-server")
                .logfileDir(configProvider.getAudit().getLogDir())
                .logfileName(configProvider.getAudit().getLogFile())
                .build());
    }

    public void audit(AuditID auditId, Map<String, Object> parameterMap) {
        AuditEntry.AuditEntryBuilder builder = AuditEntry.builder();
        AuditEntry auditEntry = builder
                .auditId(auditId)
                .attributes(parameterMap).build();
        auditLogger.log(auditEntry);
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

}
