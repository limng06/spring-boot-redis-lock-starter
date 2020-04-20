package org.springframework.boot.autoconfigure.redislock.handler.release;

import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;

public interface ReleaseTimeoutHandler {
    void handle(LockInfo lockInfo);
}
