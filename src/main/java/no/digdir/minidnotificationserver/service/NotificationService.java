package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseClient firebaseClient;
    private final RegistrationRepository registrationRepository;
    private final AuditService auditService;

    public void send(NotificationEntity notification, AdminContext adminContext) {
        registrationRepository.findByPersonIdentifierAndAppIdentifier(notification.getPerson_identifier(), notification.getApp_identifier())
                .ifPresent( device -> {
                    firebaseClient.send(notification, device.getToken());
                    auditService.auditNotificationSend(notification, adminContext);
                });

        // TODO: throw 404 if not found?
    }
}
