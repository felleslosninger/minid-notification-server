package no.digdir.minidnotificationserver.repository;

import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationDevice, Long>, JpaSpecificationExecutor<RegistrationDevice> {

    Optional<RegistrationDevice> findByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    List<RegistrationDevice> deleteByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    @Query("SELECT case when (count(d) > 0) then true else false end FROM RegistrationDevice d WHERE d.personIdentifier = ?1 AND (d.fcmToken = ?2 OR d.apnsToken = ?2)")
    boolean findByFcmTokenOrApnsToken(String personIdentifier, String token);

}
