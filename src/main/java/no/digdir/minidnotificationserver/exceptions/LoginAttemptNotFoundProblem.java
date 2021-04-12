package no.digdir.minidnotificationserver.exceptions;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class LoginAttemptNotFoundProblem extends AbstractThrowableProblem {

    public LoginAttemptNotFoundProblem(String loginAttemptId) {
        super(
                ErrorConstants.NOTFOUND_TYPE,
                "Login attempt id not found",
                Status.BAD_REQUEST,
                String.format("Login attempt id '%s' could not be found.", loginAttemptId),
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );
    }
}