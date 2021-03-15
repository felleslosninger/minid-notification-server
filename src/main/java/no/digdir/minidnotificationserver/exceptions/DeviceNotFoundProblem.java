package no.digdir.minidnotificationserver.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class DeviceNotFoundProblem extends AbstractThrowableProblem {

    public DeviceNotFoundProblem(String appId) {
        super(
          ErrorConstants.NOTFOUND_TYPE,
          "Device not found",
          Status.BAD_REQUEST,
          String.format("No device found for given 'person_identifier' where 'app_identifier' is %s", appId));
    }

}