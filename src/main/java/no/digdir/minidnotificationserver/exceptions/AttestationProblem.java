package no.digdir.minidnotificationserver.exceptions;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class AttestationProblem extends AbstractThrowableProblem {

    public AttestationProblem(String mesg) {
        super(
                ErrorConstants.ATTESTATION_REQUIRED_TYPE,
                "Invalid attestation",
                Status.BAD_REQUEST,
                String.format(mesg),
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );

    }
}