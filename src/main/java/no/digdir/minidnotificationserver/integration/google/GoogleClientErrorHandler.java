package no.digdir.minidnotificationserver.integration.google;

import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
public class GoogleClientErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(GoogleClientErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        ProblemBuilder causeBuilder = Problem.builder()
            .withType(ErrorConstants.REMOTE_API_TYPE)
            .withStatus(new HttpStatusAdapter(httpResponse.getStatusCode()))
            .withTitle(httpResponse.getStatusCode().getReasonPhrase());

        getErrorContent(httpResponse).forEach(causeBuilder::with);

        throw Problem.builder()
            .withType(ErrorConstants.DEFAULT_TYPE)
            .withStatus(BAD_REQUEST)
            .withTitle("Bad Request")
            .with("correlation_id", MDC.get("no.difi.correlationId"))
            .withCause(causeBuilder.build())
            .build();
    }



    private Map<String, Object> getErrorContent(ClientHttpResponse response) {

        String content = parseResponseBody(response);

        Map<String, Object> errorContent = new HashMap<>();

        try {
            JSONObject json = new JSONObject(content);

            // Extract violation errors
            if (json.has("status")) {
                errorContent.put("errors", json.getJSONArray("errors").toList());
            }

        } catch (JSONException ex) {
            // Do nothing about the exception. We just return an empty map.
            logger.debug("A json exception: {}", ex.getMessage());
        }

        return errorContent;
    }

    private String parseResponseBody(ClientHttpResponse response) {
        StringBuilder strBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()));
            String contentLine;
            while ((contentLine = reader.readLine()) != null) {
                strBuilder.append(contentLine).append("\n");
            }
        } catch (IOException e) {
            // do nothing
        }

        return strBuilder.toString();
    }


}

/*

POST https://fcm.googleapis.com/v1/projects/minid-authenticator/messages:send
{
  "error": {
    "code": 404,
    "message": "Requested entity was not found.",
    "status": "NOT_FOUND",
    "details": [
      {
        "@type": "type.googleapis.com/google.firebase.fcm.v1.FcmError",
        "errorCode": "UNREGISTERED"
      }
    ]
  }
}

Caused by: com.google.api.client.http.HttpResponseException: 400 Bad Request
POST https://fcm.googleapis.com/v1/projects/minid-authenticator/messages:send
{
  "error": {
    "code": 400,
    "message": "Request contains an invalid argument.",
    "status": "INVALID_ARGUMENT",
    "details": [
      {
        "@type": "type.googleapis.com/google.firebase.fcm.v1.FcmError",
        "errorCode": "INVALID_ARGUMENT"
      },
      {
        "@type": "type.googleapis.com/google.firebase.fcm.v1.ApnsError",
        "statusCode": 400,
        "reason": "BadDeviceToken"
      },
      {
        "@type": "type.googleapis.com/google.rpc.BadRequest",
        "fieldViolations": [
          {
            "field": "message.token",
            "description": "Invalid registration token"
          }
        ]
      }
    ]
  }
}


 */