package no.digdir.minidnotificationserver.exceptions;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;

public class DeviceNotFoundProblem extends AbstractThrowableProblem {

    public DeviceNotFoundProblem(String appId) {
        super(
                ErrorConstants.NOTFOUND_TYPE,
                "Device not found",
                Status.BAD_REQUEST,
                String.format("No device found for given 'person_identifier' where 'app_identifier' is '%s'", appId),
                null, // instance
                null, // cause
                ImmutableMap.of("correlation_id", MDC.get(CORRELATION_ID_HEADER))
        );
    }
}