package no.digdir.minidnotificationserver.config.developer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Profile({"dev"}) /* Only enabled when 'dev' profile is active */
@Component
public class DeveloperLoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final static Logger log = LoggerFactory.getLogger(DeveloperLoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.warn("===========================request BEGIN================================================");
        log.warn("URI         : {}", request.getURI());
        log.warn("Method      : {}", request.getMethod());
        log.warn("Headers     : {}", request.getHeaders() );
        log.warn("Request body: {}", new String(body, StandardCharsets.UTF_8));
        log.warn("==========================request END================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        HttpStatus statusCode = response.getStatusCode(); // needed here to prevent POSTs from throwing IOException in case of 4XX. Go figure...
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            // do nothing
        }
        log.warn("============================response BEGIN==========================================");

        log.warn("Status code  : {}", statusCode);
        log.warn("Status text  : {}", response.getStatusText());
        log.warn("Headers      : {}", response.getHeaders());
        log.warn("Response body: {}", inputStringBuilder);
        log.warn("============================response END============================================");
    }

}
