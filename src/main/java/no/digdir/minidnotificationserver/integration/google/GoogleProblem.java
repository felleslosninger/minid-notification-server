package no.digdir.minidnotificationserver.integration.google;

import com.google.common.collect.ImmutableMap;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class GoogleProblem extends AbstractThrowableProblem {

    public GoogleProblem(String mesg) {
        super(
                ErrorConstants.GOOGLE_TYPE,
                "Issue with Google Instance ID endpoint.", // title
                Status.BAD_REQUEST, // status code
                String.format(mesg), // detail
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );
    }

}