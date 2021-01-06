package no.digdir.minidnotificationserver.domain;

import no.digdir.minidnotificationserver.api.registration.RegistrationEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RegistrationDeviceTest {

    private static String PERSON_IDENTIFIER = "01030099326";

    @Test
    public void fromRegistrationRequestToRegistrationDevice() {

        RegistrationDevice expectedDevice = RegistrationDevice.builder()
                .personIdentifier(PERSON_IDENTIFIER)
                .appIdentifier("no.digdir.minid")
                .token("asdfqwer1234")
                .os("Android")
                .osVersion("10")
                .build();

        RegistrationEntity registrationEntity = RegistrationEntity.builder()
                .app_identifier("no.digdir.minid")
                .token("asdfqwer1234")
                .os("Android")
                .os_version("10")
                .build();
        RegistrationDevice actualDevice = RegistrationDevice.from(PERSON_IDENTIFIER, registrationEntity);

        Assert.assertEquals(expectedDevice, actualDevice);
    }


}
