package org.springframework.boot.autoconfigure.redislock.handler;

public class DistributeLockTimeOutException extends RuntimeException {
    public DistributeLockTimeOutException(String message) {
        super(message);
    }
}
