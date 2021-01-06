package no.digdir.minidnotificationserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;

import javax.persistence.*;
import java.time.Clock;

@Entity
@Data
@Table(name = "device")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegistrationDevice {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "person_identifier")
    private String personIdentifier;

    @Column(name = "app_identifier")
    private String appIdentifier;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "token")
    private String token;

    @Column(name = "os")
    private String os;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "created")
    private long created;

    @Column(name = "last_updated")
    private long lastUpdated;

    @PrePersist
    public void onPrePersist() {
        this.created = Clock.systemUTC().millis();
        this.lastUpdated = this.created;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.lastUpdated = Clock.systemUTC().millis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistrationDevice that = (RegistrationDevice) o;

        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    public RegistrationDevice from(RegistrationEntity request) {
        return this.toBuilder()
                .appIdentifier(request.getApp_identifier())
                .token(request.getToken())
                .os(request.getOs())
                .osVersion(request.getOs_version())
                .build();
    }

    public static RegistrationDevice from(String personIdentifier, RegistrationEntity request) {
        return RegistrationDevice.builder()
                .personIdentifier(personIdentifier)
                .appIdentifier(request.getApp_identifier())
                .appVersion(request.getApp_version())
                .token(request.getToken())
                .os(request.getOs())
                .osVersion(request.getOs_version())
                .build();
    }
}
