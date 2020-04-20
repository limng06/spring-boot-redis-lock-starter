package org.springframework.boot.autoconfigure.redislock.handler.lock;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;
import org.springframework.boot.autoconfigure.redislock.lock.Lock;

public interface LockTimeoutHandler {
    void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint);
}