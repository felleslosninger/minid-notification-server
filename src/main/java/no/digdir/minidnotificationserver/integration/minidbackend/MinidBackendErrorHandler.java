package no.digdir.minidnotificationserver.integration.minidbackend;

import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static no.digdir.minidnotificationserver.config.correlation.CorrelationId.CORRELATION_ID_HEADER;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public class MinidBackendErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        ProblemBuilder builder = Problem.builder()
                .withType(ErrorConstants.MINID_BACKEND_TYPE)
                .withStatus(BAD_REQUEST)
                .with("correlation_id", MDC.get(CORRELATION_ID_HEADER))
                .withTitle("Issue with MinID Backend Service endpoint.")
                .withDetail(httpResponse.getStatusCode() + ": " + httpResponse.getStatusText())
                ;

        JSONObject errorJson = getErrorContent(httpResponse);
        if(errorJson != null) {
            if(errorJson.has("code")) {
                builder.with("code", errorJson.get("code"));
            }
            if(errorJson.has("description")) {
                builder.with("description", errorJson.get("description"));
            }
            if(errorJson.has("message")) {
                builder.with("message", errorJson.get("message"));
            }
        }
        throw builder.build();
    }



    private JSONObject getErrorContent(ClientHttpResponse response) {
        try {
            String content = new BufferedReader(new InputStreamReader(response.getBody()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return new JSONObject(content);
        } catch (Exception e) {
            // do nothing
            return null;
        }
    }
}

/*
Example responses:

{
  "error": "validation_error",
  "error_descriptions": [
    {
      "field": "pid",
      "message": "USER_INVALID_SSN"
    }
  ]
}


{
  "code": "I-1003",
  "description": "The identity entered an invalid password",
  "message": "User with pid=24079425482 failed authentication, last try"
}


 */

