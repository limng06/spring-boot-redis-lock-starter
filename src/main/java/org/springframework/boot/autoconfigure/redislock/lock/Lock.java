package org.springframework.boot.autoconfigure.redislock.lock;

public interface Lock {

    /**
     * 取锁*/
    boolean acquire();

    /**
     * 释放锁*/
    boolean release();
}
