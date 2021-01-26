package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProxyConfigBean {
    private static final Logger logger = LoggerFactory.getLogger(ProxyConfigBean.class);
    private final ConfigProvider configProvider;

        @EventListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            ConfigProvider.Proxy proxy = configProvider.getProxy();
            if(proxy.isEnabled()) {
                System.setProperty("https.proxyHost", proxy.getHost());
                System.setProperty("https.proxyPort", proxy.getPort());
                System.setProperty("com.google.api.client.should_use_proxy", "true");
                logger.info("Using proxy at {}", proxy.getHost() + ":" + proxy.getPort() + ".");
            } else {
                logger.info("No proxy configured.");
            }
        }

}
