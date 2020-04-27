package org.springframework.boot.autoconfigure.redislock;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.redislock.config.RedisLockConfig;
import org.springframework.boot.autoconfigure.redislock.core.RedisLockAspectConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = RedisLockConfig.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@EnableConfigurationProperties(RedisLockConfig.class)
@Import({RedisLockAspectConfig.class, RedisLockConfiguration.class})
public class RedisLockAutoConfiguration {
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        return null;
    }
}
