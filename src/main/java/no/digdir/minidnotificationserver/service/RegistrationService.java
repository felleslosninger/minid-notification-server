package no.digdir.minidnotificationserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final AuditService auditService;
    private final GoogleClient googleClient;

    public RegistrationEntity upsertDevice(String personIdentifier, RegistrationEntity entity) {

        RegistrationDevice savedOrUpdatedDevice;
        Optional<RegistrationDevice> optDevice = registrationRepository.findByPersonIdentifierAndAppIdentifier(personIdentifier, entity.getApp_identifier());

        if("ios".equalsIgnoreCase(entity.getOs()) && entity.getApns_token().isEmpty()) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            auditService.auditRegistrationServiceImportApnsToken(entity, personIdentifier, fcmToken);
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }

        if(optDevice.isPresent()) { // update existing
            RegistrationDevice existingDevice = deepCopy(optDevice.get());
            savedOrUpdatedDevice = registrationRepository.save(existingDevice.from(entity));
            auditService.auditRegistrationServiceUpdateDevice(existingDevice, savedOrUpdatedDevice);
        } else { // create new
            RegistrationDevice device = RegistrationDevice.from(personIdentifier, entity);
            savedOrUpdatedDevice = registrationRepository.save(device);
            auditService.auditRegistrationServiceRegisterDevice(savedOrUpdatedDevice);
        }

        return RegistrationEntity.from(savedOrUpdatedDevice);
    }

    public void deleteDevice(String personIdentifier, RegistrationEntity entity) {
        List<RegistrationDevice> deletedDevices = registrationRepository.deleteByPersonIdentifierAndAppIdentifier(personIdentifier, entity.getApp_identifier());
        if(deletedDevices.size() > 0) {
            RegistrationDevice deletedDevice = deletedDevices.get(0);
            auditService.auditRegistrationServiceDeleteDevice(deletedDevice);
        }
    }

    private RegistrationDevice deepCopy(RegistrationDevice device) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(device), RegistrationDevice.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problem with deepCopy.");
        }
    }

}
