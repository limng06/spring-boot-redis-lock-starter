package org.springframework.boot.autoconfigure.redislock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = RedisLockConfig.PREFIX)
public class RedisLockConfig {
    public static final String PREFIX = "spring.redis-lock";
    /**
     * 默认等待2S时间 获取不到就返回异常*/
    private long waitTime = 2000;
    /**
     * 默认9秒自动释放锁*/
    private long leaseTime = 9000;

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }
}
