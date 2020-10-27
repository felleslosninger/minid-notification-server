package no.digdir.minidnotificationserver.service;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationRequest;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public void registerDevice(RegistrationRequest request) {

        RegistrationDevice device = registrationRepository.findByToken(request.getToken()).orElse(new RegistrationDevice());

        device.setPersonIdentifier(request.getPerson_identifier());
        device.setToken(request.getToken());
        device.setDescription(request.getDescription());
        device.setOs(request.getOs());
        device.setOsVersion(request.getOs_version());

        registrationRepository.save(device);
    }
}
