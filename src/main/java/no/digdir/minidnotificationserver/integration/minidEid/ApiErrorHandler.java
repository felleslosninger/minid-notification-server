package no.digdir.minidnotificationserver.integration.minidEid;

import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public class ApiErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        ProblemBuilder builder = Problem.builder()
                .withType(ErrorConstants.MINID_TYPE)
                .withStatus(BAD_REQUEST)
                .with("correlation_id", MDC.get("no.difi.correlationId"))
                .withTitle("Issue with MinID eID endpoint.")
                .withDetail(httpResponse.getStatusCode() + ": " + httpResponse.getStatusText())
                ;
        throw builder.build();
    }
}
