package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.exceptions.ExceptionHandlerFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .cors(withDefaults())
                .csrf().disable()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // translate unauthenticated exception to a http status 401 response
            .and()
                .headers()
                // check with online CSP evaluator at https://csp-evaluator.withgoogle.com/
//                .contentSecurityPolicy("base-uri 'self'; object-src 'none'; default-src 'self'; connect-src 'self' localhost; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' ; font-src  'self' ; img-src 'self' data: ;  frame-ancestors 'none'; child-src 'self'; frame-src 'self'; require-trusted-types-for 'script")
                //TODO: check csp and swagger
//            .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
            .and()
                .and()
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
            .and()
                .authorizeRequests()
                .antMatchers("/api/**", "/info", "/version", "/health", "/prometheus", "/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui/index.html")
                .permitAll()
            .and()
                .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(this.tokenAuthenticationManagerResolver()))
        ;

    }


    @Bean
    AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver() {
        OpaqueTokenAuthenticationProvider opaqueTokenAuthenticationProvider = new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector);
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(this.issuerUri));

        List<String> pathsThatUseOpaqueTokens = Arrays.asList("/api/register/device");

        return request -> {
            if (pathsThatUseOpaqueTokens.contains(request.getRequestURI())) {
                return opaqueTokenAuthenticationProvider::authenticate;
            } else {
                return jwtAuthenticationProvider::authenticate;
            }
        };
    }


}
