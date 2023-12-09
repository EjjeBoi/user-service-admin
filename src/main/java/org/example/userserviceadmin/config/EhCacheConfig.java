package org.example.userserviceadmin.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.example.userserviceadmin.model.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class EhCacheConfig {

    @Value("${cache.time-to-live}")
    private long timeToLive;

    @Value("${cache.max-entries}")
    private long maxEntries;

    @Bean
    public javax.cache.CacheManager customCacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager();

        cacheManager.createCache("users", Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, UserEntity.class,
                                ResourcePoolsBuilder.heap(maxEntries))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(timeToLive)))
                        .build()));
        return cacheManager;
    }
}