package no.digdir.minidnotificationserver.config;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@EnableCaching(proxyTargetClass = true)
@ConditionalOnExpression("'${spring.cache.type}'!='none'")
public class CacheConfiguration {


    @Value("${cache.local.ttl-in-s:5}")
    private int localTtl;

    @Value("${cache.cluster.ttl-in-s:300}")
    private int clusterTtl;

    @Value("${cache.cluster.transport.file.location}")
    private String location;


    @Bean("globalCacheConfig")
    public ConfigurationBuilderHolder globalConfiguration() {
        ConfigurationBuilderHolder holder = new ConfigurationBuilderHolder();
        holder.getCurrentConfigurationBuilder()
                .expiration()
                .lifespan(clusterTtl, SECONDS)
                .clustering().cacheMode(CacheMode.REPL_SYNC);
        holder.getGlobalConfigurationBuilder()
                .transport().addProperty("configurationFile", location + "cache-transport.xml")
                .clusterName("notification-cluster")
                .build();
        return holder;
    }

    @Bean
    @Primary
    public EmbeddedCacheManager localCacheManager() {
        ConfigurationBuilderHolder holder = new ConfigurationBuilderHolder();
        holder.getCurrentConfigurationBuilder().expiration().lifespan(localTtl, SECONDS).build();
        return new DefaultCacheManager(holder, true);
    }

}
