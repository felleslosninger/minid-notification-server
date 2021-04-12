package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.domain.Device;
import no.digdir.minidnotificationserver.repository.DeviceRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Component
@RequiredArgsConstructor
public class AppTokenAuthenticationProvider implements AuthenticationProvider {

	private final DeviceRepository deviceRepository;
	private final List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("APP_AUTHORIZE"), new SimpleGrantedAuthority("APP_DEVICE"));

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String appToken = (String) authentication.getPrincipal();

		Optional<Device> device = deviceRepository.findByFcmTokenOrApnsToken(appToken);

		if (device.isPresent()) {
			PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(device.get().getPersonIdentifier(), "ROLE_USER", authorities);
			authenticationToken.setDetails(Collections.singletonMap("token", appToken));
			return authenticationToken;
		} else {
			return null;
		}

	}


	@Override
	public boolean supports(Class<?> authentication) {
		return PreAuthenticatedAuthenticationToken.class.equals(authentication);
	}

}