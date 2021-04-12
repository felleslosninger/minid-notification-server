package no.digdir.minidnotificationserver.api.authorization;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import no.digdir.minidnotificationserver.api.internal.authorization.RequestAuthorizationEntity;
import no.digdir.minidnotificationserver.integration.firebase.FirebaseBeans;
import no.digdir.minidnotificationserver.logging.audit.AuditService;
import no.digdir.minidnotificationserver.service.NotificationServerCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@SpringBootTest
public class AuthorizationEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    @MockBean
    private NotificationServerCache cache;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @MockBean
    public JwtDecoder jwtDecoder;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private FirebaseBeans firebaseBeans;

    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @Test
    public void approvalTest() throws Exception {
        String token = "testabc";
        String input = "{ \"request_id\": \"request-id_1\"}";
        Jwt testJWT = getJwt("person_id", "minid:app.register");
        when(jwtDecoder.decode(token)).thenReturn(testJWT);
        when(cache.getLoginAttempt("request-id_1")).thenReturn(getApprovalRequest());

        ResultActions resultActions = mockMvc.perform(post("/api/authorization/approve")
                .header("Authorization", "Bearer " + token)
                .header("Client-id", "")
                .header("Client-secret", "")
                .content(input)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(status().isUnauthorized());

    }

    private RequestAuthorizationEntity getApprovalRequest() {
        return RequestAuthorizationEntity.builder()
                .person_identifier("person_identifier")
                .login_attempt_id("request-id")
                .login_attempt_counter(1)
                .service_provider("Supperådet")
                .build();
    }

    Jwt getJwt(String pid, String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("pid", pid);
        claims.put("scope", scope);
        Map<String, Object> headers = new HashMap<>();
        headers.put("testheader", "test");
        return new Jwt("lol", Instant.now(), Instant.now().plusSeconds(50L), headers, claims);
    }


}
