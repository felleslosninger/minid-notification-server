package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
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

    public void send(NotificationEntity notification, AdminContext adminContext) {
        Optional<RegistrationDevice> optDev = registrationRepository.findByPersonIdentifierAndAppIdentifier(notification.getPerson_identifier(), notification.getApp_identifier());

        if(optDev.isPresent()) {
            RegistrationDevice device = optDev.get();
            firebaseClient.send(notification, device.getFcmToken());
            auditService.auditNotificationSend(notification, adminContext);
        } else {
            log.debug("No device found.");
            // TODO: throw 404 if not found?
        }


    }
}
