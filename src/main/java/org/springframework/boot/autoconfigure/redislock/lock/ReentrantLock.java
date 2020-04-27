package org.springframework.boot.autoconfigure.redislock.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author limng
 * 非公平-可重入锁
 */
public class ReentrantLock implements Lock {
    private final LockInfo lockInfo;
    Logger logger = LoggerFactory.getLogger(ReentrantLock.class);
    private RLock rLock;
    private RedissonClient redissonClient;

    public ReentrantLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    public boolean acquire() {
        try {
            logger.info("trying to get lock" + lockInfo.getName());
            rLock = redissonClient.getLock(lockInfo.getName());
            boolean accRes = rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.MILLISECONDS);
            return accRes;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release() {
        if (rLock.isHeldByCurrentThread()) {
            try {
                logger.info("trying to release lock" + lockInfo.getName());
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException e) {
                logger.error("ReentrantLock release InterruptedException", e);
                return false;
            } catch (ExecutionException e) {
                logger.error("ReentrantLock release ExecutionException", e);
                return false;
            }
        }
        return false;
    }

    public String getKey() {
        return this.lockInfo.getName();
    }
}
