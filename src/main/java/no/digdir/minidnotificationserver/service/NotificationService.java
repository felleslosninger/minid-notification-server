package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.internal.notification.NotificationEntity;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.exceptions.DeviceNotFoundProblem;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
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
    private final RegistrationRepository registrationRepository;
    private final AuditService auditService;

    @Value("${mock.notification.enabled}")
    private Boolean mockEnabled;

    public void send(NotificationEntity notification, AdminContext adminContext) {
        Optional<RegistrationDevice> optDev = registrationRepository.findByPersonIdentifierAndAppIdentifier(notification.getPerson_identifier(), notification.getApp_identifier());
        log.debug("request_approval - notificationservice");
        if(optDev.isPresent()) {
            RegistrationDevice device = optDev.get();
            firebaseClient.send(notification, device.getFcmToken());
            auditService.auditNotificationSend(notification, adminContext);
        } else if (mockEnabled) {
            //TODO: Ta bort senere, heller bedre testdata
            firebaseClient.send(notification, notification.getBody());
        } else {
            log.debug("No device found.");
            throw new DeviceNotFoundProblem(notification.getApp_identifier());
        }


    }
}
