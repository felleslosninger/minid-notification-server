package no.digdir.minidnotificationserver.domain;

import no.digdir.minidnotificationserver.api.device.DeviceEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DeviceTest {

    private static String PERSON_IDENTIFIER = "01030099326";

    @Test
    public void fromRegistrationRequestToRegistrationDevice() {

        Device expectedDevice = Device.builder()
                .personIdentifier(PERSON_IDENTIFIER)
                .appIdentifier("no.digdir.minid")
                .fcmToken("asdfqwer1234")
                .os("Android")
                .osVersion("10")
                .build();

        DeviceEntity deviceEntity = DeviceEntity.builder()
                .app_identifier("no.digdir.minid")
                .token("asdfqwer1234")
                .os("Android")
                .os_version("10")
                .build();
        Device actualDevice = Device.from(PERSON_IDENTIFIER, deviceEntity);

        Assert.assertEquals(expectedDevice, actualDevice);
    }


}
