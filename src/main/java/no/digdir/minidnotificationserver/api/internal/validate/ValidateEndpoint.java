package no.digdir.minidnotificationserver.api.internal.validate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.service.ValidationService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class ValidateEndpoint {

    private final ValidationService validationService;

    @Operation(summary = "Verify pid and token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @PostMapping("/validate")
    public ResponseEntity<String> registerDevice(@RequestBody ValidateEntity validateEntity) {
        boolean result = validationService.validate(validateEntity);
        return ResponseEntity.status(HttpStatus.OK).body(new JSONObject().put("result", result).toString());
    }

}