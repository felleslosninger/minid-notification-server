package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public RegistrationDevice registerDevice(String personIdentifier, RegistrationEntity request) {
        return registrationRepository.save(RegistrationDevice.from(personIdentifier, request));
    }

    public void updateDevice(String personIdentifier, RegistrationEntity request) {
        registrationRepository.findByPersonIdentifierAndAppIdentifier(personIdentifier, request.getApp_identifier())
                .ifPresent(device -> registrationRepository.save(device.from(request)));
    }

    public Long deleteDevice(String personIdentifier, RegistrationEntity request) {
        return registrationRepository.deleteByPersonIdentifierAndToken(personIdentifier, request.getToken());
    }

}
