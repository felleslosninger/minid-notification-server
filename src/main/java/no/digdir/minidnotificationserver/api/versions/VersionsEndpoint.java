package no.digdir.minidnotificationserver.api.versions;

import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
//@SecurityRequirement(name = "notification_auth")
public class VersionsEndpoint {

    private final ConfigProvider configProvider;

    @GetMapping("/app/versions")
    public ResponseEntity<ConfigProvider.AppVersions> getVersions() {
        ConfigProvider.AppVersions cfg = configProvider.getAppVersions();
        return new ResponseEntity<ConfigProvider.AppVersions>(cfg, HttpStatus.OK);
    }
}
