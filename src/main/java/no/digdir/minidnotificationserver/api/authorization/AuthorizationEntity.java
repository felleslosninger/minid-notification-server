package no.digdir.minidnotificationserver.api.authorization;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationEntity {

    @Schema(description = "The login attempt id", example = "[uuid-4]")
    String login_attempt_id;

    @Schema(description = "The login attempt counter", example = "1")
    Integer login_attempt_counter;

}


