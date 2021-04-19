package no.digdir.minidnotificationserver.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.*;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import java.net.URI;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;
import static no.digdir.minidnotificationserver.exceptions.ErrorConstants.UNKNOWN_TYPE;

@ControllerAdvice
@Slf4j
public class ExceptionHandler implements ProblemHandling, SecurityAdviceTrait {

    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {

        if(status.equals(Status.INTERNAL_SERVER_ERROR)) { // only handle internal server error
            ThrowableProblem cause = Problem.builder()
                    .withStatus(status)
                    .withTitle(status.getReasonPhrase())
                    .withDetail(throwable.getMessage())
                    .withType(type)
                    .build();

            return Problem.builder()
                    .withTitle("Unknown error occurred.")
                    .withStatus(Status.BAD_REQUEST)
                    .withType(UNKNOWN_TYPE)
                    .with("correlation_id", MDC.get(CORRELATION_ID_HEADER))
                    .withCause(cause) // wrap unknown errors in cause
                    ;
        } else {
            return ProblemHandling.super.prepare(throwable, status, type);
        }
    }

    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request, HttpStatus status) {
        log.debug("Exception occurred", throwable);
    }


}