package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.config.filters.CSPNonceFilter;
import no.digdir.minidnotificationserver.exceptions.ExceptionHandlerFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .cors(withDefaults()) // TODO: check me
                .csrf().disable() // TODO: check me
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // TODO: check me
                .headers()
                // check with online CSP evaluator at https://csp-evaluator.withgoogle.com/
                .contentSecurityPolicy("base-uri 'self'; object-src 'none'; default-src 'self'; connect-src 'self' https: ; script-src 'nonce-{{nonce}}' 'strict-dynamic' http: https: 'unsafe-inline'; style-src 'self' 'nonce-{{nonce}}'; font-src 'self'; img-src 'self' data:;  frame-ancestors 'none'; child-src 'self'; frame-src 'self';")
            .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
            .and()
                .and()
                .addFilterBefore(new CSPNonceFilter(), HeaderWriterFilter.class)
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
            .and()
                .authorizeRequests()
                .antMatchers("/info", "/version", "/health", "/prometheus", "/favicon.ico", "/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui/index.html", "/swagger-ui.html", "/api/onboarding/**","/api/app/versions", "/api/internal/**", "/api/attestation/**")
                .permitAll()
            .and()
                .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer().opaqueToken()
        ;

    }

}
