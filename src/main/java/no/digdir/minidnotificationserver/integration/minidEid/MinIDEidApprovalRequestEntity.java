package no.digdir.minidnotificationserver.integration.minidEid;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MinIDEidApprovalRequestEntity {
    private String personal_identity;
}
