package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.notification.NotificationRequest;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseClient firebaseClient;
    private final RegistrationRepository registrationRepository;

    public void send(NotificationRequest request) {
        registrationRepository.findByPersonIdentifier(request.getPerson_identifier())
                .ifPresent( device -> firebaseClient.send(request, device.getToken()));
    }
}
