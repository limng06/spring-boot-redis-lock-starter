package org.springframework.boot.autoconfigure.redislock.strategy;

import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;
import org.springframework.boot.autoconfigure.redislock.handler.DistributeLockTimeOutException;
import org.springframework.boot.autoconfigure.redislock.handler.release.ReleaseTimeoutHandler;

/**
 * @author limng
 */
public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler {
    /**
     * 默认处理逻辑 继续执行业务逻辑
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 抛出异常
     */
    FAST_FAILURE() {
        @Override
        public void handle(LockInfo lockInfo) {
            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            throw new DistributeLockTimeOutException(errorMsg);
        }
    }
}
