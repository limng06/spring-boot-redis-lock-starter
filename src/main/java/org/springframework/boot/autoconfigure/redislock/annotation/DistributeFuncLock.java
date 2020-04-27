package org.springframework.boot.autoconfigure.redislock.annotation;

import org.springframework.boot.autoconfigure.redislock.enums.LockScope;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DistributeLock(key = "", scope = LockScope.Function)
@Order(1)
public @interface DistributeFuncLock {
}
