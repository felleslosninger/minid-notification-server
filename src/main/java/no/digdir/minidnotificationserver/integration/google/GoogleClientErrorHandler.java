package no.digdir.minidnotificationserver.integration.google;

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

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public class GoogleClientErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        ProblemBuilder builder = Problem.builder()
                .withType(ErrorConstants.GOOGLE_TYPE)
                .withStatus(BAD_REQUEST)
                .with("correlation_id", MDC.get("no.difi.correlationId"))
                .withTitle("Issue with Google Instance ID endpoint.")
                .withDetail(httpResponse.getStatusCode() + ": " + httpResponse.getStatusText())

                ;

        JSONObject errorJson = getErrorContent(httpResponse);
        if(errorJson != null && errorJson.has("error")) {
            builder.withDetail(httpResponse.getStatusCode() + ": " + errorJson.get("error"));
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
  "type": "https://digdir.no/problem/google-api",
  "title": "Issue with Google Instance ID endpoint.",
  "status": 400,
  "detail": "400 BAD_REQUEST: Malformed request",
  "correlation_id": "1c1795da-70c2-4444-968f-dfc2690691b4"
}

{
  "type": "https://digdir.no/problem/google-api",
  "title": "Issue with Google Instance ID endpoint.",
  "status": 400,
  "detail": "401 UNAUTHORIZED: Unauthorized",
  "correlation_id": "426cd511-0b08-421e-8bdf-880aaf4e928d"
}
 */
