package no.digdir.minidnotificationserver.integration.minidservice;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinIdServiceClient {
    private final ConfigProvider configProvider;

    public boolean verify_password(String pid, String password) {
        return true;
    }

    public boolean verify_code(String pid, String code) {
        return true;
    }

}
