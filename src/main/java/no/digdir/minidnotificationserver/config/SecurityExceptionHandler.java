package no.digdir.minidnotificationserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import java.net.URI;
import java.util.Optional;

@ControllerAdvice
public class SecurityExceptionHandler implements ProblemHandling, SecurityAdviceTrait {

    private final static Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {

        if(status.getStatusCode() == 400 || status.getStatusCode() == 500) {
            log.warn(throwable.getMessage(), throwable);
        }

        return Problem.builder()
                .withType(type)
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(throwable.getMessage())
                .withCause(Optional.ofNullable(throwable.getCause())
                        .filter(cause -> isCausalChainsEnabled())
                        .map(this::toProblem)
                        .orElse(null));
    }

    @Override
    public ResponseEntity<Problem> handleThrowable(Throwable throwable, NativeWebRequest request) {
        return create(throwable, request);
    }
}