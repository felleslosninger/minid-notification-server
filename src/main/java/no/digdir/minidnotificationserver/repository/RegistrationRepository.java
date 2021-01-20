package no.digdir.minidnotificationserver.repository;

import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationDevice, Long>, JpaSpecificationExecutor<RegistrationDevice> {

    Optional<RegistrationDevice> findByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    List<RegistrationDevice> deleteByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    Optional<RegistrationDevice> findByPersonIdentifier(String personIdentifier);

}
