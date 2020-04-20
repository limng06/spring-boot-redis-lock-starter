package org.springframework.boot.autoconfigure.redislock.core;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.redislock.annotation.DistributeLock;
import org.springframework.boot.autoconfigure.redislock.config.RedisLockConfig;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;

public class LockInfoGenerator {
    LockKeyGenerator lockKeyGenerator;
    RedisLockConfig redisLockConfig;

    public LockInfoGenerator(RedisLockConfig config, LockKeyGenerator lockKeyGenerator) {
        this.redisLockConfig = config;
        this.lockKeyGenerator = lockKeyGenerator;
    }


    public LockInfo generate(JoinPoint point, DistributeLock lock) {
        LockInfo lockInfo = new LockInfo();
        lockInfo.setType(lock.lockType());
        lockInfo.setScope(lock.scope());
        String name = lockKeyGenerator.getKeyName(point, lock);
        lockInfo.setName(name);
        return lockInfo;
    }
}
