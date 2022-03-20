package io.hotcloud.common.spring;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.cache.CacheProperties;
import io.hotcloud.common.cache.CaffeineCache;
import io.hotcloud.common.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
@Slf4j
public class CacheConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = CacheProperties.PROPERTIES_TYPE_NAME,
            havingValue = "local",
            matchIfMissing = true
    )
    public Cache caffeineCache() {
        return new CaffeineCache(null);
    }

    @Bean
    @ConditionalOnProperty(
            name = CacheProperties.PROPERTIES_TYPE_NAME,
            havingValue = "redis"
    )
    public Cache redisCache() {
        return new RedisCache();
    }
}
