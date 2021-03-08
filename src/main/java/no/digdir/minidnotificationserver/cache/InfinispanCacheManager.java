package no.digdir.minidnotificationserver.cache;

import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class InfinispanCacheManager extends DefaultCacheManager {

    public InfinispanCacheManager(@Qualifier("globalCacheConfig") ConfigurationBuilderHolder globalConfiguration) {
        super(globalConfiguration, true);
    }
}
