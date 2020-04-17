package org.springframework.boot.autoconfigure.redislock.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FairLock implements Lock {
    private final LockInfo lockInfo;
    private RLock rLock;
    private RedissonClient redissonClient;

    public FairLock(RedissonClient redissonClient, LockInfo info) {
        this.redissonClient = redissonClient;
        this.lockInfo = info;
    }

    public boolean acquire() {
        try {
            rLock = redissonClient.getFairLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getMaxTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean release() {
        if (rLock.isHeldByCurrentThread()) {

            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            }
        }
        return false;
    }
}
