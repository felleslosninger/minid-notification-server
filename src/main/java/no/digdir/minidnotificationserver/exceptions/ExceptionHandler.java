package no.digdir.minidnotificationserver.exceptions;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

@ControllerAdvice
public class ExceptionHandler implements ProblemHandling {

    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
        return Problem.builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(throwable.getMessage())
                .withType(type)
                .with("correlation_id", MDC.get(CORRELATION_ID_HEADER));
    }
}