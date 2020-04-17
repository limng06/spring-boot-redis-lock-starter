package org.springframework.boot.autoconfigure.redislock.lock;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;

public class LockFacotry {
    RedissonClient redissonClient;

    public LockFacotry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public Lock get(LockInfo lockInfo) {

        switch (lockInfo.getType()) {
            case Fair:
                return new FairLock(redissonClient, lockInfo);
        }
        return null;
    }
}
