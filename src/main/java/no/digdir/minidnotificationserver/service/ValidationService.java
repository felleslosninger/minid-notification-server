package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.internal.validate.ValidateEntity;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final DeviceRepository deviceRepository;

    @Audit(auditId = AuditID.VALIDATE_PID_TOKEN)
    public boolean validate(ValidateEntity validateEntity) {
        return deviceRepository.findByFcmTokenOrApnsToken(validateEntity.getPerson_identifier(), validateEntity.getToken());
    }
}
