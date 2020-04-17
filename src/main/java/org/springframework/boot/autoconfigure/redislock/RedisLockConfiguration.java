package org.springframework.boot.autoconfigure.redislock;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.redislock.config.RedisLockConfig;
import org.springframework.boot.autoconfigure.redislock.lock.LockFacotry;
import org.springframework.boot.autoconfigure.redislock.utils.ApplicationContextHelper;
import org.springframework.boot.autoconfigure.redislock.core.LockInfoGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisLockConfiguration {

    @Bean
    public LockInfoGenerator lockInfoGenerator(RedisLockConfig redisLockConfig) {
        return new LockInfoGenerator(redisLockConfig);
    }

    @Bean
    public LockFacotry lockFacotry() {
        RedissonClient redissonClient = ApplicationContextHelper.getBean(RedissonClient.class);
        return new LockFacotry(redissonClient);
    }

    @Bean
    public RedisLockConfig redisLockConfig() {
        return new RedisLockConfig();
    }

    @Bean
    public ApplicationContextHelper getApplicationContextHelper() {
        return new ApplicationContextHelper();
    }
}
