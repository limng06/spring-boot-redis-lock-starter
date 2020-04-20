package org.springframework.boot.autoconfigure.redislock.strategy;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;
import org.springframework.boot.autoconfigure.redislock.handler.DistributeLockTimeOutException;
import org.springframework.boot.autoconfigure.redislock.handler.lock.LockTimeoutHandler;
import org.springframework.boot.autoconfigure.redislock.lock.Lock;

public enum LockTimeoutStrategy implements LockTimeoutHandler {
    /**
     * 继续执行业务逻辑
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            // do nothing
        }
    },
    /**
     * 默认处理逻辑 获取锁超时返回失败
     */
    FAST_FAILURE() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new DistributeLockTimeOutException(errorMsg);
        }
    }
}
