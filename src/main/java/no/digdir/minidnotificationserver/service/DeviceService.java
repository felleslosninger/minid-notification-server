package no.digdir.minidnotificationserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import no.digdir.minidnotificationserver.domain.Device;
import no.digdir.minidnotificationserver.integration.google.GoogleClient;
import no.digdir.minidnotificationserver.integration.minidbackend.MinIdBackendClient;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.logging.event.EventService;
import no.digdir.minidnotificationserver.repository.DeviceRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final AuditService auditService;
    private final EventService eventService;
    private final GoogleClient googleClient;
    private final MinIdBackendClient minIdBackendClient;

    @Deprecated
    public DeviceEntity upsertDevice(String personIdentifier, DeviceEntity entity) {

        Device savedOrUpdatedDevice;
        Optional<Device> optDevice = deviceRepository.findByPersonIdentifierAndAppIdentifier(personIdentifier, entity.getApp_identifier());

        if("ios".equalsIgnoreCase(entity.getOs()) && Strings.isBlank(entity.getApns_token())) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }

        if(optDevice.isPresent()) { // update existing
            Device existingDevice = deepCopy(optDevice.get());
            savedOrUpdatedDevice = deviceRepository.save(existingDevice.from(entity));
            auditService.auditRegistrationServiceUpdateDevice(existingDevice, savedOrUpdatedDevice);
        } else { // create new
            Device device = Device.from(personIdentifier, entity);
            savedOrUpdatedDevice = deviceRepository.save(device);
            auditService.auditRegistrationServiceRegisterDevice(savedOrUpdatedDevice);
        }

        return DeviceEntity.from(savedOrUpdatedDevice);
    }



    @Audit(auditId = AuditID.DEVICE_CREATE)
    public DeviceEntity save(String personIdentifier, DeviceEntity entity) {
        if("ios".equalsIgnoreCase(entity.getOs()) && Strings.isBlank(entity.getApns_token())) {
            String fcmToken = googleClient.importAPNsToken(entity.getToken());
            entity.setApns_token(entity.getToken());
            entity.setToken(fcmToken);
        }
        Device device = Device.from(personIdentifier, entity);
        Device savedDevice = deviceRepository.save(device);
        return DeviceEntity.from(savedDevice);
    }

    @Audit(auditId = AuditID.DEVICE_UPDATE)
    public DeviceEntity update(String personIdentifier, DeviceEntity entity) {
        Device updatedDevice;
        Optional<Device> optDevice = deviceRepository.findByPersonIdentifierAndAppIdentifier(personIdentifier, entity.getApp_identifier());

        if(optDevice.isPresent()) { // update existing
            Device existingDevice = deepCopy(optDevice.get());
            updatedDevice = deviceRepository.save(existingDevice.from(entity));
            eventService.logUserHasUpdatedDevice(personIdentifier);
            return DeviceEntity.from(updatedDevice);
        }
        return null;
    }


    @Deprecated
    public int deleteByAppId(String personIdentifier, DeviceEntity entity) {
        return deleteByAppId(personIdentifier, entity.getApp_identifier());
    }

    @Audit(auditId = AuditID.DEVICE_DELETE)
    public int deleteByAppId(String personIdentifier, String appId) {
        List<Device> deletedDevices = deviceRepository.deleteByPersonIdentifierAndAppIdentifier(personIdentifier, appId);
        if(deletedDevices.size() > 0) {
            minIdBackendClient.setPreferredTwoFactorMethod(personIdentifier, "pin"); // TODO: temporary reset to 'pin'
        }
        return deletedDevices.size();
    }

    @Audit(auditId = AuditID.DEVICE_DELETE)
    public int delete(String personIdentifier, String token) {
        List<Device> deletedDevices = deviceRepository.deleteByFcmTokenOrApnsToken(token, token);
        if(deletedDevices.size() > 0) {
            eventService.logUserHasDeletedDevice(personIdentifier);
            minIdBackendClient.setPreferredTwoFactorMethod(personIdentifier, "pin"); // TODO: temporary reset to 'pin'
        }
        return deletedDevices.size();
    }

    private Device deepCopy(Device device) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(device), Device.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problem with deepCopy.");
        }
    }

}
