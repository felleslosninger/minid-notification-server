package no.digdir.minidnotificationserver.exceptions;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class AppVersionProblem extends AbstractThrowableProblem {

    public AppVersionProblem(String mesg, URI type) {
        super(
                type,
                "Issues with app version headers.",
                Status.BAD_REQUEST,
                String.format(mesg),
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );

    }
}