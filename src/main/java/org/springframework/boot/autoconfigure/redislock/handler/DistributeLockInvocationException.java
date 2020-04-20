package org.springframework.boot.autoconfigure.redislock.handler;

import org.springframework.boot.autoconfigure.redislock.annotation.DistributeLock;

public class DistributeLockInvocationException extends RuntimeException{
    public DistributeLockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
