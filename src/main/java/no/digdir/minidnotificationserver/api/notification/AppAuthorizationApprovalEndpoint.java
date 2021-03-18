package no.digdir.minidnotificationserver.api.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.api.ValidateVersionHeaders;
import no.digdir.minidnotificationserver.api.internal.approval.RequestApprovalEntity;
import no.digdir.minidnotificationserver.api.internal.validate.ValidateEntity;
import no.digdir.minidnotificationserver.service.AuthenticationService;
import no.digdir.minidnotificationserver.service.RequestApprovalCache;
import no.digdir.minidnotificationserver.service.ValidationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_OS_HEADER;
import static no.digdir.minidnotificationserver.api.ValidateVersionHeadersAspect.MINID_APP_VERSION_HEADER;

@RestController
@RequestMapping("/api/authorization")
@RequiredArgsConstructor
@SecurityRequirement(name = "notification_auth")
@ValidateVersionHeaders
public class AppAuthorizationApprovalEndpoint {
    private final RequestApprovalCache requestApprovalCache;
    private final AuthenticationService authenticationService;
    private final ValidationService validationService;

    @Operation(summary = "Approve request for authorization to app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful approval, no content returned."),
            @ApiResponse(responseCode = "404", description = "RequestID not found in cache, approval rejected"),
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')") //TODO: Inntil videre. Skal byttes ut SNART
    public ResponseEntity<String> approve(@RequestHeader HttpHeaders headers, @RequestBody AppAuthorizationApprovalEntity approvalEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        RequestApprovalEntity notification = requestApprovalCache.getApprovalRequestForLoginAttempt(approvalEntity.getRequestId());
        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
        }
        //TODO: Få grep om token for validering
//        validationService.validate(getValidateEntity(notification, "token"), AdminContext.of(headers, principal));
        if (false) {
            //TODO: Valider data fra token mot kobling mot requestId
        } else {
            return authenticationService.sendApproval(notification.getPerson_identifier());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Reject request for authorization to app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful rejection, no content returned."),
            @ApiResponse(responseCode = "404", description = "RequestID not found in cache, rejection not relayed to minid-authentication-web"),
    })
    @Parameters( value = {
            @Parameter(in = ParameterIn.HEADER, description = "Version of MinID App", name = MINID_APP_VERSION_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "1.0.1"))),
            @Parameter(in = ParameterIn.HEADER, description = "Operating system of MinID App", name = MINID_APP_OS_HEADER, content = @Content(schema = @Schema(type = "string", required = true, defaultValue = "Android", allowableValues = {"Android", "iOS"})))
    })
    @PostMapping("/reject")
    @PreAuthorize("hasAuthority('SCOPE_minid:app.register')") //TODO: Inntil videre. Skal byttes ut SNART
    public ResponseEntity<String> reject(@RequestHeader HttpHeaders headers, @RequestBody AppAuthorizationApprovalEntity approvalEntity, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        RequestApprovalEntity notification = requestApprovalCache.getApprovalRequestForLoginAttempt(approvalEntity.getRequestId());
        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
        }
        //TODO: Få grep om token for validering
//        validationService.validate(getValidateEntity(notification, "token"), AdminContext.of(headers, principal));
        if (false) {
            //TODO: Valider data fra token mot kobling mot requestId
        } else {
            return authenticationService.sendRejection(notification.getPerson_identifier());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ValidateEntity getValidateEntity(RequestApprovalEntity requestApprovalEntity, String token) {
        return ValidateEntity.builder()
                .person_identifier(requestApprovalEntity.getPerson_identifier())
                .login_attempt_id(requestApprovalEntity.getKey())
                .build();

    }
}
