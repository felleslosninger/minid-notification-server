package no.digdir.minidnotificationserver.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import no.digdir.minidnotificationserver.api.notification.NotificationRequest;
import no.digdir.minidnotificationserver.domain.RegistrationDevice;
import no.digdir.minidnotificationserver.repository.RegistrationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private RegistrationRepository registrationRepository;

    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @MockBean
    private AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver;

    @MockBean
    private JwtDecoder jwtDecoderByIssuerUri;


    @Captor
    private ArgumentCaptor<Message> messageCaptor;


    @Before
    public void setUp() throws FirebaseMessagingException {
        RegistrationDevice registrationDevice = RegistrationDevice.builder()
                .token("snazzytoken1234")
                .personIdentifier("01030099326")
                .build();
        Mockito.when(registrationRepository.findByPersonIdentifier(anyString())).thenReturn(registrationDevice);

        Mockito.when(firebaseMessaging.send(any())).thenReturn("msgId-1234");
    }

    @Test
    public void sendMessageTest() throws FirebaseMessagingException, NoSuchFieldException, IllegalAccessException {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .title("My snazzy title")
                .message("My even snazzier expectedMessage")
                .person_identifier("01030099326")
                .build();
        notificationService.send(notificationRequest);

        Message expectedMessage = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("My snazzy title")
                        .setBody("My even snazzier expectedMessage")
                        .setImage("https://idporten.difi.no/error/images/svg/eid.svg")
                        .build())
                .setToken("snazzytoken1234")
                .build();
        Mockito.verify(firebaseMessaging).send(messageCaptor.capture());

        Assert.assertTrue(new ReflectionEquals(expectedMessage, "notification").matches(messageCaptor.getValue()));

        // notification field is non-public...
        Field notificationField = Message.class.getDeclaredField("notification");
        notificationField.setAccessible(true); // hehe...
        Notification expectedNotification = (Notification) notificationField.get(expectedMessage);
        Notification actualNotification = (Notification) notificationField.get(messageCaptor.getValue());
        Assert.assertTrue(new ReflectionEquals(expectedNotification).matches(actualNotification));

    }
}
