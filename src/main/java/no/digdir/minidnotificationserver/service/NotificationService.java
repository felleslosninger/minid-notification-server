package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.domain.Device;
import no.digdir.minidnotificationserver.exceptions.DeviceNotFoundProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.logging.audit.Audit;
import no.digdir.minidnotificationserver.logging.audit.AuditID;
import no.digdir.minidnotificationserver.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseClient firebaseClient;
    private final DeviceRepository deviceRepository;

    @Value("${mock.notification.enabled}")
    private Boolean mockEnabled;

    @Audit(auditId = AuditID.NOTIFICATION_SERVICE)
    public void send(NotificationEntity notification) {
        Optional<Device> optDev = deviceRepository.findByPersonIdentifierAndAppIdentifier(notification.getPerson_identifier(), notification.getApp_identifier());

        if(optDev.isPresent()) {
            Device device = optDev.get();
            firebaseClient.send(notification, device.getFcmToken());
        } else if (mockEnabled) {
            //TODO: Ta bort senere, heller bedre testdata
            firebaseClient.send(notification, notification.getBody());
        } else {
            log.debug("No device found.");
            throw new DeviceNotFoundProblem(notification.getApp_identifier());
        }


    }
}
