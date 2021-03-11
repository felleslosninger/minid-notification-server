package no.digdir.minidnotificationserver.config.developer;

import lombok.extern.slf4j.Slf4j;
import no.digdir.minidnotificationserver.config.CacheConfiguration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.cachelistener.annotation.*;
import org.infinispan.notifications.cachelistener.event.*;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"dev"}) /* Only enabled when 'dev' profile is active */
@ConditionalOnExpression("'${infinispan.embedded.enabled}'=='true'")
@Component
public class DeveloperCacheListener  {

    public DeveloperCacheListener(CacheManager cacheManager, CacheConfiguration cacheConfiguration) {
        EmbeddedCacheManager  manager = ((SpringEmbeddedCacheManager) cacheManager).getNativeCacheManager();
        manager.addListener(new DeveloperCacheListener.Listener());
        manager.getCache(cacheConfiguration.getCacheNameOnboarding()).addListener(new DeveloperCacheListener.Listener());
        manager.getCache(cacheConfiguration.getCacheNameLoginAttempts()).addListener(new DeveloperCacheListener.Listener());
    }

    @org.infinispan.notifications.Listener(clustered = true)
    class Listener {

        @CacheEntryLoaded
        public void getKey(CacheEntryLoadedEvent event) {
            log.debug("Loaded entry with key: " + event.getKey() + ", and value: " + event.getValue());
        }

        @CacheEntryVisited
        public void visitKey(CacheEntryVisitedEvent event) {
            if (event.isPre()) {
                log.debug("Visited entry with key: " + event.getKey() + ", and value: " + event.getValue());
            }
        }

        @CacheEntryCreated
        public void addKey(CacheEntryCreatedEvent event) {
            if (!event.isPre()) {
                log.debug("Created entry with key: " + event.getKey() + ", and value: " + event.getValue());
            }
        }

        @CacheEntryRemoved
        public void removeKey(CacheEntryRemovedEvent event) {
            log.debug("Removed entry with key: " + event.getKey());
        }


        @CacheEntryModified
        public void modifiedKey(CacheEntryModifiedEvent event) {
            if (event.isCreated()) {
                log.debug("Created entry with key: " + event.getKey() + ", and value: " + event.getValue());
            } else if (event.isPre()) { // old value
                if (event.getValue() != null) {
                    log.debug("Modified entry with key: " + event.getKey() + ", and old value: " + event.getValue());
                }
            } else { // new value
                log.debug("Modified entry with key: " + event.getKey() + ", and new value: " + event.getValue());
            }
        }

        @CacheEntryExpired
        public void expiredKey(CacheEntryExpiredEvent event) {
            log.debug("Expired entry with key: " + event.getKey());
        }

        @CacheStarted
        public void cacheStarted(CacheStartedEvent event) {
            log.debug("Started cache " + event.getCacheName());
        }

        @CacheStopped
        public void cacheStopped(CacheStoppedEvent event) {
            log.debug("Stopped cache " + event.getCacheName());
        }
    }
}