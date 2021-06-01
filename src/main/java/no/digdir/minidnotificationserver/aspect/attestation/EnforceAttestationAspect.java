package no.digdir.minidnotificationserver.aspect.attestation;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.domain.TokenEntity;
import no.digdir.minidnotificationserver.domain.Device;
import no.digdir.minidnotificationserver.exceptions.AttestationProblem;
import no.digdir.minidnotificationserver.repository.DeviceRepository;
import no.digdir.minidnotificationserver.service.NotificationServerCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class EnforceAttestationAspect {

    private final DeviceRepository deviceRepository;
    private final NotificationServerCache cache;

    // @EnforceAttestation can be placed on class or method
    @Before("within(@no.digdir.minidnotificationserver.aspect.attestation.EnforceAttestation *) || @annotation(no.digdir.minidnotificationserver.aspect.attestation.EnforceAttestation)")
    public void validateAttestation(JoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String[] parameterNames = signature.getParameterNames(); // method parameter names
        Object[] parameterValues = joinPoint.getArgs(); // method parameter values

        String token = null;
        for (Object value : parameterValues) {
            if (value instanceof TokenEntity) {
                token = ((TokenEntity) value).getToken();
                break;
            }
        }

        // special case for 'DELETE /api/device/{appId}'
        if(token == null) {
            String personIdentifier = null;
            String appId = null;
            for (int i = 0; i < parameterValues.length; i++) {
                if (parameterValues[i] instanceof OAuth2AuthenticatedPrincipal) {
                    personIdentifier = ((OAuth2AuthenticatedPrincipal)parameterValues[i]).getAttribute("pid");
                } else if ("appId".equals(parameterNames[i]) && parameterValues[i] instanceof String) {
                    appId = (String) parameterValues[i];
                }
            }

            if(appId != null && personIdentifier != null) {
                Optional<Device> optDevice = deviceRepository.findByPersonIdentifierAndAppIdentifier(personIdentifier, appId);
                if(optDevice.isPresent()) {
                    Device device = optDevice.get();
                    token = device.getOs().equalsIgnoreCase("android") ? device.getFcmToken() : device.getApnsToken();

                }
            }
        }

        if(token == null || token.isEmpty() || cache.getAttestation(token) == null) {
            throw new AttestationProblem("Attestation missing for token '" + token + "'");
        }

    }
}
