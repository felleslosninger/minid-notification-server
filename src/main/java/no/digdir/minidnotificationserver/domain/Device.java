package no.digdir.minidnotificationserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.time.Clock;

@Entity
@Data
@Table(name = "device")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Device {

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


    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "apns_token")
    private String apnsToken;

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

        Device that = (Device) o;

        return fcmToken.equals(that.fcmToken);
    }

    @Override
    public int hashCode() {
        return fcmToken.hashCode();
    }

    public Device from(DeviceEntity entity) {
        Device.DeviceBuilder builder = this.toBuilder();

        if(Strings.isNotBlank(entity.getApp_identifier())) {
            builder.appIdentifier(entity.getApp_identifier());
        }

        if(Strings.isNotBlank(entity.getApp_version())) {
            builder.appVersion(entity.getApp_version());
        }

        if(Strings.isNotBlank(entity.getToken())) {
            builder.fcmToken(entity.getToken());
        }

        if(Strings.isNotBlank(entity.getApns_token())) {
            builder.apnsToken(entity.getApns_token());
        }

        if(Strings.isNotBlank(entity.getOs())) {
            builder.os(entity.getOs());
        }

        if(Strings.isNotBlank(entity.getOs_version())) {
            builder.osVersion(entity.getOs_version());
        }

        return builder.build();
    }

    public static Device from(String personIdentifier, DeviceEntity entity) {
        return Device.builder()
                .personIdentifier(personIdentifier)
                .appIdentifier(entity.getApp_identifier())
                .appVersion(entity.getApp_version())
                .fcmToken(entity.getToken())
                .apnsToken(entity.getApns_token())
                .os(entity.getOs())
                .osVersion(entity.getOs_version())
                .build();
    }
}
