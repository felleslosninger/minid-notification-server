package no.digdir.minidnotificationserver.config;

import lombok.RequiredArgsConstructor;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.spring.starter.embedded.InfinispanCacheConfigurer;
import org.infinispan.spring.starter.embedded.InfinispanGlobalConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Configuration
@EnableCaching(proxyTargetClass = true)
@ConditionalOnExpression("'${infinispan.embedded.enabled}'=='true'")
public class EmbeddedCacheConfiguration {

    @Value("${cache.local.ttl-in-s:5}")
    private int localTtl;

    @Value("${cache.cluster.ttl-in-s:300}")
    private int clusterTtl;

    @Value("${cache.cluster.transport.file-location}")
    private String fileLocation;

    private final CacheConfiguration cacheConfiguration;

    @Bean
    public InfinispanGlobalConfigurer infinispanGlobalConfigurer() {
        return new GlobalConfigurationBuilder()
                .transport().defaultTransport()
                .addProperty("configurationFile", fileLocation)
                .clusterName("NOTIFICATION_CLUSTER")::build;
    }

    @Bean
    public InfinispanCacheConfigurer cacheConfigurer() {
        return manager -> {
            final org.infinispan.configuration.cache.Configuration ispnConfig = new ConfigurationBuilder()
                    .clustering()
                    .cacheMode(CacheMode.REPL_SYNC)
                    .build();
            manager.createCache(cacheConfiguration.getCachePrefix() + "-sessions", ispnConfig);
            manager.defineConfiguration(cacheConfiguration.getCacheNameLoginAttempts(),
                    new ConfigurationBuilder()
                            .simpleCache(true)
                            .expiration()
                            .lifespan(clusterTtl, SECONDS)
                            .build());

            manager.defineConfiguration(cacheConfiguration.getCacheNameOnboarding(),
                    new ConfigurationBuilder()
                            .simpleCache(true)
                            .expiration()
                            .lifespan(clusterTtl, SECONDS)
                            .build());


        };
    }

}
