package no.digdir.minidnotificationserver.service;

import com.google.firebase.messaging.*;
import no.digdir.minidnotificationserver.api.notification.NotificationEntity;
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
import java.util.HashMap;
import java.util.Map;

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
        Mockito.when(registrationRepository.findByPersonIdentifier(anyString())).thenReturn(java.util.Optional.ofNullable(registrationDevice));

        Mockito.when(firebaseMessaging.send(any())).thenReturn("msgId-1234");
    }

    @Test
    public void sendMessageTest() throws FirebaseMessagingException, NoSuchFieldException, IllegalAccessException {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key1", "value1");
        dataMap.put("key2", "value2");
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .title("My snazzy title")
                .body("My even snazzier expectedMessage")
                .person_identifier("01030099326")
                .app_identifier("no.minid.app")
                .aps_category("MINID_AUTH_CATEGORY")
                .click_action("minid_auth_intent")
                .data(dataMap)
                .build();
        notificationService.send(notificationEntity);

        Message expectedMessage = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("My snazzy title")
                        .setBody("My even snazzier expectedMessage")
                        .setImage("https://idporten.difi.no/error/images/svg/eid.svg")
                        .build())
                .setToken("snazzytoken1234")
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setCategory("MINID_AUTH_CATEGORY")
                                .build())
                        .putHeader("apns-priority", "5")
                        .putHeader("apns-expiration", "2419200")
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("minid_auth_intent")
                                .build())
                        .setTtl(2419200L * 1000)
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .build())
                .putAllData(dataMap)
                .build();
        Mockito.verify(firebaseMessaging).send(messageCaptor.capture());

        Assert.assertTrue(new ReflectionEquals(expectedMessage, "notification", "apnsConfig", "androidConfig").matches(messageCaptor.getValue()));

        // notification, apnsConfig & androidConfig fields is non-public...
        Field field = Message.class.getDeclaredField("notification");
        field.setAccessible(true); // hehe...
        Notification expectedNotification = (Notification) field.get(expectedMessage);
        Notification actualNotification = (Notification) field.get(messageCaptor.getValue());
        Assert.assertTrue(new ReflectionEquals(expectedNotification).matches(actualNotification));

        field = Message.class.getDeclaredField("apnsConfig");
        field.setAccessible(true);
        ApnsConfig expectedApnsConfig = (ApnsConfig) field.get(expectedMessage);
        ApnsConfig actualApnsConfig = (ApnsConfig) field.get(messageCaptor.getValue());
        Assert.assertTrue(new ReflectionEquals(expectedApnsConfig).matches(actualApnsConfig));

        field = Message.class.getDeclaredField("androidConfig");
        field.setAccessible(true);
        AndroidConfig expectedAndroidConfig = (AndroidConfig) field.get(expectedMessage);
        AndroidConfig actualAndroidConfig = (AndroidConfig) field.get(messageCaptor.getValue());
        Assert.assertTrue(new ReflectionEquals(expectedAndroidConfig, "notification").matches(actualAndroidConfig));

    }
}
