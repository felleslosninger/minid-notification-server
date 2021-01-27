package no.digdir.minidnotificationserver.integration.google;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoogleResponseEntity {
    private List<ListItem> results = new ArrayList<>();

    @Data
    public static class ListItem {
        private String apns_token;
        private String status;
        private String registration_token;
    }
}