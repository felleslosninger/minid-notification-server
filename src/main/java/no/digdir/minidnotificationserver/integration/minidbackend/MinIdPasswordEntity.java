package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinIdPasswordEntity {

    @Data
    @Builder

    public static class Request {
        String pid;
        String password;
    }

}
