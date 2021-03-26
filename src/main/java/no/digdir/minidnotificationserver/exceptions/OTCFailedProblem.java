package no.digdir.minidnotificationserver.exceptions;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class OTCFailedProblem extends AbstractThrowableProblem {

    public OTCFailedProblem(String mesg) {
        super(
                ErrorConstants.OTC_FAILED,
                "One-time-code not accepted.",
                Status.BAD_REQUEST,
                String.format(mesg),
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );

    }
}