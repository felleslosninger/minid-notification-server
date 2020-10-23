package no.digdir.minidnotificationserver.config;

import no.digdir.minidnotificationserver.config.developer.DeveloperAuthenticationSuccessHandler;
import no.digdir.minidnotificationserver.exceptions.ExceptionHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final DeveloperAuthenticationSuccessHandler devAuthenticationSuccessHandler;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository,
                          @Nullable DeveloperAuthenticationSuccessHandler devAuthenticationSuccessHandler,
                          ExceptionHandlerFilter exceptionHandlerFilter,
                          SecurityProblemSupport problemSupport) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.devAuthenticationSuccessHandler = devAuthenticationSuccessHandler;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
        this.problemSupport = problemSupport;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionFixation().migrateSession()
            .and()
                .cors(withDefaults())
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .logout()
                .logoutSuccessHandler(new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository))
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // needed to allow GET if CSRF is configured
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // translate unauthenticated exception to a http status 401 response
            .and()
                .headers()
                // check with online CSP evaluator at https://csp-evaluator.withgoogle.com/
                .contentSecurityPolicy("base-uri 'self'; object-src 'none'; default-src 'self'; connect-src 'self' localhost; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' ; font-src  'self' ; img-src 'self' data: ;  frame-ancestors 'none'; child-src 'self'; frame-src 'self'; require-trusted-types-for 'script")
            .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
            .and()
                .and()
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
            .and()
                .authorizeRequests()
                .antMatchers("/health", "/info", "/version").permitAll()
                .antMatchers("/api/**").authenticated()
            .and()
                .oauth2Login()
                .successHandler(devAuthenticationSuccessHandler != null ? devAuthenticationSuccessHandler : new SimpleUrlAuthenticationSuccessHandler("/") ) // only enabled in 'dev'-profile
        ;

    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

}
