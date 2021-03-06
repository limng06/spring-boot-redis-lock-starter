package org.springframework.boot.autoconfigure.redislock.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.redislock.annotation.DistributeFuncLock;
import org.springframework.boot.autoconfigure.redislock.annotation.DistributeLock;
import org.springframework.boot.autoconfigure.redislock.entity.LockInfo;
import org.springframework.boot.autoconfigure.redislock.handler.DistributeLockInvocationException;
import org.springframework.boot.autoconfigure.redislock.lock.Lock;
import org.springframework.boot.autoconfigure.redislock.lock.LockFacotry;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order(0)
public class RedisLockAspectConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisLockAspectConfig.class);
    private final Map<String, LockRes> currentThreadLock = new ConcurrentHashMap<>();
    @Autowired
    LockInfoGenerator lockInfoGenerator;
    @Autowired
    LockFacotry lockFacotry;

    @Around(value = "@annotation(distributeLock)")
    public Object aroundDistributeLock(ProceedingJoinPoint point, DistributeLock distributeLock) throws Throwable {
        return around(point, distributeLock);
    }

    @AfterReturning(value = "@annotation(lock) || @annotation(lock)")
    public void afterReturning(JoinPoint joinPoint, DistributeLock lock) throws Throwable {
        String curentLock = this.getCurrentLockId(joinPoint, lock);
        releaseLock(lock, joinPoint, curentLock);
        cleanUpThreadLocal(curentLock);
    }

    @AfterThrowing(value = "@annotation(lock)|| @annotation(lock)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, DistributeLock lock, Throwable ex) throws Throwable {
        String curentLock = this.getCurrentLockId(joinPoint, lock);
        releaseLock(lock, joinPoint, curentLock);
        cleanUpThreadLocal(curentLock);
        throw ex;
    }

    @Around(value = "@annotation(distributeFuncLock)")
    public Object aroundDistributeFuncLock(ProceedingJoinPoint point, DistributeFuncLock distributeFuncLock) throws Throwable {
        DistributeLock distributeLock = distributeFuncLock.annotationType().getAnnotation(DistributeLock.class);
        // 获取 lockInfo 信息
        return around(point, distributeLock);
    }

    @AfterReturning(value = "@annotation(lock)")
    public void afterReturningDistributeFuncLock(JoinPoint joinPoint, DistributeFuncLock lock) throws Throwable {
        releaseDistributeFuncLock(joinPoint, lock);
    }

    @AfterThrowing(value = "@annotation(lock)", throwing = "ex")
    public void afterThrowingDistributeFuncLock(JoinPoint joinPoint, DistributeFuncLock lock, Throwable ex) throws Throwable {
        releaseDistributeFuncLock(joinPoint, lock);
        throw ex;
    }

    private void releaseDistributeFuncLock(JoinPoint joinPoint, DistributeFuncLock lock) throws Throwable {
        DistributeLock distributeLock = lock.annotationType().getAnnotation(DistributeLock.class);
        String curentLock = this.getCurrentLockId(joinPoint, distributeLock);
        releaseLock(distributeLock, joinPoint, curentLock);
        cleanUpThreadLocal(curentLock);
    }

    private Object around(ProceedingJoinPoint point, DistributeLock distributeLock) throws Throwable {
        // 获取 lockInfo 信息
        LockInfo lockInfo = lockInfoGenerator.generate(point, distributeLock);
        //
        String curentLock = this.getCurrentLockId(point, distributeLock);
        currentThreadLock.put(curentLock, new LockRes(lockInfo, false));
        Lock lock = lockFacotry.get(lockInfo);
        boolean result = lock.acquire();
        if (!result) {
            if (logger.isWarnEnabled()) {
                logger.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
            distributeLock.lockTimeoutStrategy().handle(lockInfo, lock, point);
        } else {
            logger.info("get lock: " + lockInfo.getName());
        }

        currentThreadLock.get(curentLock).setLock(lock);
        currentThreadLock.get(curentLock).setRes(true);

        return point.proceed();
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new DistributeLockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     * 释放锁
     */
    private void releaseLock(DistributeLock lock, JoinPoint joinPoint, String curentLock) throws Throwable {
        LockRes lockRes = currentThreadLock.get(curentLock);
        if (Objects.isNull(lockRes)) {
            throw new NullPointerException("Please check whether the input parameter used as the lock key value has been modified in the method, which will cause the acquire and release locks to have different key values and throw null pointers.curentLockKey:" + curentLock);
        }
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get(curentLock).getLock().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(lock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }

    // avoid memory leak
    private void cleanUpThreadLocal(String curentLock) {
        currentThreadLock.remove(curentLock);
    }

    /**
     * 获取当前锁在map中的key
     *
     * @param joinPoint
     * @param lock
     * @return
     */
    private String getCurrentLockId(JoinPoint joinPoint, DistributeLock lock) {
        LockInfo lockInfo = lockInfoGenerator.generate(joinPoint, lock);
        String curentLock = Thread.currentThread().getId() + lockInfo.getName();
        return curentLock;
    }

    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(DistributeLock lock, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {
        if (logger.isWarnEnabled()) {
            logger.warn("Timeout while release Lock({})", lockInfo.getName());
        }
        lock.releaseTimeoutStrategy().handle(lockInfo);
    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new DistributeLockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private class LockRes {

        private LockInfo lockInfo;
        private Lock lock;
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }

        LockInfo getLockInfo() {
            return lockInfo;
        }

        void setLockInfo(LockInfo lockInfo) {
            this.lockInfo = lockInfo;
        }

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        Boolean getRes() {
            return res;
        }

        void setRes(Boolean res) {
            this.res = res;
        }
    }
}
