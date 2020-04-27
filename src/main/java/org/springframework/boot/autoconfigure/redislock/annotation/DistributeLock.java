package org.springframework.boot.autoconfigure.redislock.annotation;

import org.springframework.boot.autoconfigure.redislock.enums.LockScope;
import org.springframework.boot.autoconfigure.redislock.enums.LockType;
import org.springframework.boot.autoconfigure.redislock.strategy.LockTimeoutStrategy;
import org.springframework.boot.autoconfigure.redislock.strategy.ReleaseTimeoutStrategy;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * Created by 黎明 on 2018/12/13.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Order(1)
public @interface DistributeLock {
    LockScope scope() default LockScope.Key;

    LockType lockType() default LockType.Reetrant;

    String prefix() default "Key:";

    String key() default "";

    /**
     * 尝试加锁，最多等待时间
     *
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 上锁以后xxx秒自动解锁
     *
     * @return leaseTime
     */
    long leaseTime() default Long.MIN_VALUE;

    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.FAST_FAILURE;

    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;
}
