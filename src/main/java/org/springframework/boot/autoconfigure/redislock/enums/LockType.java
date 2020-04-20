package org.springframework.boot.autoconfigure.redislock.enums;

public enum LockType {

    /**
     * 公平锁
     */
    Fair,

    /**
     * 可重入锁，默认
     */
    Reetrant;
}
