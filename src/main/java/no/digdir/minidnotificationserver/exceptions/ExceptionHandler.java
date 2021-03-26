package no.digdir.minidnotificationserver.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

@ControllerAdvice
@Slf4j
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


    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request, HttpStatus status) {
        log.debug("Exception occurred: {}", throwable.toString());
    }

}