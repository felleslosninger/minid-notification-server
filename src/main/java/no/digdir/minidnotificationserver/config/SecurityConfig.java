package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.exceptions.ExceptionHandlerFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
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
                .antMatchers("/info", "/version", "/health", "/prometheus", "/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/swagger-ui/index.html", "/swagger-ui.html")
                .permitAll()
            .and()
                .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer().opaqueToken()
        ;

    }

}
