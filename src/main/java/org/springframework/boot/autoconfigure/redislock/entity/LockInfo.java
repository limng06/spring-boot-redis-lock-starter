package org.springframework.boot.autoconfigure.redislock.entity;

import org.springframework.boot.autoconfigure.redislock.enums.LockScope;
import org.springframework.boot.autoconfigure.redislock.enums.LockType;

import java.io.Serializable;

public class LockInfo implements Serializable {
    private LockScope scope;
    private LockType type;
    private String name;
    private long waitTime;
    private long maxTime;

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public LockScope getScope() {
        return scope;
    }

    public void setScope(LockScope scope) {
        this.scope = scope;
    }
}
