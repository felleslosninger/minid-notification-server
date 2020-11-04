package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.notification.NotificationRequest;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseClient;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseClient firebaseClient;
    private final RegistrationRepository registrationRepository;

    public void send(NotificationRequest request) {
        List<RegistrationDevice> deviceList = registrationRepository.findByPersonIdentifier(request.getPerson_identifier());
        List<String> tokenList = deviceList.stream().map(RegistrationDevice::getToken).collect(Collectors.toList());

        firebaseClient.send(request, tokenList);
    }
}
