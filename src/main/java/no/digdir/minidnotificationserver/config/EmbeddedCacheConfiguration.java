package no.digdir.minidnotificationserver.config;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.spring.starter.embedded.InfinispanCacheConfigurer;
import org.infinispan.spring.starter.embedded.InfinispanGlobalConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@EnableCaching(proxyTargetClass = true)
@ConditionalOnExpression("'${infinispan.embedded.enabled}'=='true'")
public class EmbeddedCacheConfiguration {

    @Value("${cache.local.ttl-in-s:5}")
    private int localTtl;

    @Value("${cache.cluster.ttl-in-s:300}")
    private int clusterTtl;
    @Autowired
    private CacheConfiguration cacheConfiguration;

    @Bean
    public InfinispanGlobalConfigurer infinispanGlobalConfigurer() {
        return new GlobalConfigurationBuilder()
                .transport().clusterName("LOCAL_CLUSTER")::build;
    }

    @Bean
    public InfinispanCacheConfigurer cacheConfigurer() {
        return manager -> {
            final org.infinispan.configuration.cache.Configuration ispnConfig = new ConfigurationBuilder()
                    .clustering()
                    .cacheMode(CacheMode.LOCAL)
                    .build();
            manager.createCache(cacheConfiguration.getCachePrefix() + "-sessions", ispnConfig);
            manager.defineConfiguration(cacheConfiguration.getCacheNameLoginAttempts(),
                    new ConfigurationBuilder()
                            .simpleCache(true)
                            .expiration()
                            .lifespan(clusterTtl, SECONDS)
                            .build());
        };
    }

}
