package org.springframework.boot.autoconfigure.redislock.entity;

import org.springframework.boot.autoconfigure.redislock.enums.LockScope;
import org.springframework.boot.autoconfigure.redislock.enums.LockType;

import java.io.Serializable;

public class LockInfo implements Serializable {
    private LockScope scope;
    private LockType type;
    private String name;
    private long waitTime;
    private long leaseTime;

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

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public LockScope getScope() {
        return scope;
    }

    public void setScope(LockScope scope) {
        this.scope = scope;
    }
}
