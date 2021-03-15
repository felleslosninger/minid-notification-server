package no.digdir.minidnotificationserver.api.notification;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppAuthorizationApprovalEntity {
    // max payload is 4KB

    @Schema(description = "The login attempt id", example = "[uuid-4]")
    @JsonProperty("request_id")
    String requestId;

    public String getLoginAttemptId() {
        if (StringUtils.isEmpty(requestId) || requestId.contains("_")) {
            return null;
        } else {
            return requestId.split("_")[0];
        }
    }

    public String getLoginAttemptCounter() {
        if (StringUtils.isEmpty(requestId) || requestId.contains("_")) {
            return null;
        } else {
            return requestId.split("_")[1];
        }
    }

}


