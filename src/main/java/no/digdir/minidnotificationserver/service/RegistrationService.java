package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationRequest;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public RegistrationDevice registerDevice(String personIdentifier, RegistrationRequest request) {
        return registrationRepository.save(RegistrationDevice.from(personIdentifier, request));
    }

    public Long deleteDevice(String token) {
        return registrationRepository.deleteByToken(token);
    }

    public void updateDevice(String token, RegistrationRequest request) {
        registrationRepository.findByToken(token)
                .ifPresent(device -> registrationRepository.save(device.from(request)));
    }
}
