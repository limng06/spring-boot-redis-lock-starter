package org.springframework.boot.autoconfigure.redislock.enums;

public enum LockScope {
    /**
     * Key 表示结合 key属性和 DLockKey 生成最后的锁名称
     * 常规使用*/
    Key,
    /**
     * Function 表示 结合方法名和方法参数 生成最后的锁名称
     * 适合用户多线程调用下避免重复调用*/
    Function;
}
