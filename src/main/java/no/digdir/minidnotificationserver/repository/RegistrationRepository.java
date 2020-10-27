package no.digdir.minidnotificationserver.repository;

import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationDevice, Long>, JpaSpecificationExecutor<RegistrationDevice> {

    @Query("select d.id from RegistrationDevice d where d.token = :token")
    Optional<Long> findIdByToken(@Param("token") String token);

    Optional<RegistrationDevice> findByToken(String token);
}
