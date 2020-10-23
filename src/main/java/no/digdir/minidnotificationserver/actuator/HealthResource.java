package no.digdir.minidnotificationserver.actuator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthResource {

    @JsonProperty(value = "status")
    private String status;

}
