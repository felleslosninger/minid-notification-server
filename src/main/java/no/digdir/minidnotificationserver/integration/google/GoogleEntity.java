package no.digdir.minidnotificationserver.integration.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleEntity {

    @Data
    @Builder
    static class Request {
        private String application;
        private boolean sandbox;
        private Set<String> apns_tokens;
    }

    @Data
    static class Response {
        public List<ListItem> results = new ArrayList<>();

        @Data
        static class ListItem {
            private String apns_token;
            private String status;
            private String registration_token;
        }
    }

}
