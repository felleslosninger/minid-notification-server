package no.digdir.minidnotificationserver.domain;

import no.digdir.minidnotificationserver.api.registration.RegistrationRequest;
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
                .description("Some description")
                .os("Android")
                .osVersion("10")
                .build();

        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .app_identifier("no.digdir.minid")
                .token("asdfqwer1234")
                .description("Some description")
                .os("Android")
                .os_version("10")
                .build();
        RegistrationDevice actualDevice = RegistrationDevice.from(PERSON_IDENTIFIER, registrationRequest);

        Assert.assertEquals(expectedDevice, actualDevice);
    }


}
