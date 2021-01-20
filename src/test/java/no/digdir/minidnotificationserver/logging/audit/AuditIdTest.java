package no.digdir.minidnotificationserver.logging.audit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

@DisplayName("When using audit id")
public class AuditIdTest {

    @DisplayName("All numeric ids should be unique")
    @Test
    public void testUniqueNumericId() {
        Assertions.assertEquals(AuditID.values().length,
                Arrays.asList(AuditID.values())
                        .stream()
                        .map(auditId -> auditId.id())
                        .collect(Collectors.toSet())
                        .size());
    }

    @DisplayName("All audit ids should be unique")
    @Test
    public void testUniqueAuditId() {
        Assertions.assertEquals(AuditID.values().length,
                Arrays.asList(AuditID.values())
                        .stream()
                        .map(auditId -> auditId.auditId())
                        .collect(Collectors.toSet())
                        .size());
    }

    @DisplayName("All audit ids should contain prefix and numeric id")
    @Test
    public void testAuditIdContainsPrefixAndNumericId() {
        for(AuditID auditID : AuditID.values()) {
            Assertions.assertEquals("MINID-NOTIFICATION-" + auditID.id() + "-" + auditID.name().replace('_', '-'), auditID.auditId());
        }
    }

}
