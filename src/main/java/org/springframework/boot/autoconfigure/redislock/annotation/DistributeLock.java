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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(1)
public @interface DistributeLock {
    LockScope scope() default LockScope.Key;
    LockType lockType() default LockType.Fair;
    String prefix() default "Key:";
    String key();
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.FAST_FAILURE;
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;
}
