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

    public void registerDevice(RegistrationRequest registrationRequest) {

        RegistrationDevice device = RegistrationDevice.builder()
                .personIdentifier(registrationRequest.getPerson_identifier())
                .description(registrationRequest.getDescription())
                .token(registrationRequest.getToken())
                .os(registrationRequest.getOs())
                .osVersion(registrationRequest.getOs_version())
                .build();

        registrationRepository.saveAndFlush(device);
    }
}
