package org.springframework.boot.autoconfigure.redislock.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.redislock.annotation.DistributeLock;
import org.springframework.boot.autoconfigure.redislock.config.RedisLockConfig;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;
import org.springframework.boot.autoconfigure.redislock.enums.LockScope;

import java.util.HashMap;

public class LockInfoGenerator {
    LockKeyGenerator lockKeyGenerator;
    RedisLockConfig redisLockConfig;

    public LockInfoGenerator(RedisLockConfig config, LockKeyGenerator lockKeyGenerator) {
        this.redisLockConfig = config;
        this.lockKeyGenerator = lockKeyGenerator;
    }


    public LockInfo generate(ProceedingJoinPoint point, DistributeLock lock) {
        LockInfo lockInfo = new LockInfo();
        lockInfo.setType(lock.lockType());
        lockInfo.setScope(lock.scope());
        String name = lockKeyGenerator.getKeyName(point, lock);
        lockInfo.setName(name);
        return lockInfo;
    }
}
