package no.digdir.minidnotificationserver.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

public final class OpaqueMinIdTokenAuthenticationProvider implements AuthenticationProvider {

	private OpaqueTokenAuthenticationProvider opaqueTokenAuthenticationProvider;

	public OpaqueMinIdTokenAuthenticationProvider(OpaqueTokenIntrospector introspector) {
		this.opaqueTokenAuthenticationProvider = new OpaqueTokenAuthenticationProvider(introspector);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication authenticate = opaqueTokenAuthenticationProvider.authenticate(authentication);
		if(authenticate instanceof BearerTokenAuthentication) {
			BearerTokenAuthentication bearerAuth = (BearerTokenAuthentication) authenticate;

			/* Should check amr=Minid* but checks acr=Level3 for now. This is to prevent logins through other than MinID. */
			String acrLevel = (String) bearerAuth.getTokenAttributes().get("acr");
			if(!"Level3".equalsIgnoreCase(acrLevel)) {
				throw new BadCredentialsException("Authentication Context Class Reference or Authentication Method Reference does not belong to MinID.");
			}
		}
		return authenticate;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return opaqueTokenAuthenticationProvider.supports(authentication);
	}
}

