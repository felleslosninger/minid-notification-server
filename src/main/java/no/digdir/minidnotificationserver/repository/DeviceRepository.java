package no.digdir.minidnotificationserver.repository;

import no.digdir.minidnotificationserver.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    Optional<Device> findByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    List<Device> deleteByPersonIdentifierAndAppIdentifier(String personIdentifier, String appIdentifier);

    @Query("SELECT case when (count(d) > 0) then true else false end FROM Device d WHERE d.personIdentifier = ?1 AND (d.fcmToken = ?2 OR d.apnsToken = ?2)")
    boolean findByFcmTokenOrApnsToken(String personIdentifier, String token);

    @Query("SELECT d FROM Device d WHERE d.fcmToken = ?1 OR d.apnsToken = ?1")
    Optional<Device>  findByFcmTokenOrApnsToken(String token);

    @Modifying
    @Query("DELETE FROM Device d WHERE d.fcmToken = ?1 OR d.apnsToken = ?1")
    List<Device> deleteByFcmTokenOrApnsToken(String token);

}
