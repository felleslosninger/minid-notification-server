package no.digdir.minidnotificationserver.integration.google;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GoogleRequestEntity {
    private String application;
    private boolean sandbox;
    private Set<String> apns_tokens;
}
