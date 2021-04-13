package no.digdir.minidnotificationserver.integration.minidbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Preferred2FaMethodEntity {
    @Data
    @Builder
    static class Request {
        String preferred2FaMethod; // otc, pin, app
    }
}
