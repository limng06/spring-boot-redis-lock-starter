package org.springframework.boot.autoconfigure.redislock.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.redislock.annotation.DLockKey;
import org.springframework.boot.autoconfigure.redislock.annotation.DistributeLock;
import org.springframework.boot.autoconfigure.redislock.config.RedisLockConfig;
import org.springframework.boot.autoconfigure.redislock.enums.LockScope;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LockKeyGenerator {
    private final RedisLockConfig redisLockConfig;
    private final String FUNCTION_PREFIX = "FUNC:";
    /**
     * 所有锁的前缀
     */
    private final String MAD_PREFIX = "RedisLock:";
    private ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private ExpressionParser parser = new SpelExpressionParser();

    public LockKeyGenerator(RedisLockConfig redisLockConfig) {
        this.redisLockConfig = redisLockConfig;
    }

    public String getKeyName(JoinPoint joinPoint, DistributeLock lock) {
        List<String> keyList = new ArrayList<>();
        Method method = getMethod(joinPoint);
        if (LockScope.Function.equals(lock.scope())) {
            String name = FUNCTION_PREFIX + method.toGenericString();
            if (joinPoint.getArgs().length > 0) {
                HashMap<Integer, Object> map = new HashMap<Integer, Object>();
                Object[] args = joinPoint.getArgs();
                for (int i = 0; i < args.length; i++) {
                    map.put(Integer.valueOf(i), args[i]);
                }
                name = name + map.hashCode();
            }
            return MAD_PREFIX + name;
        } else {
            String definitionKey = getSpelDefinitionKey(lock.key(), method, joinPoint.getArgs());
            keyList.add(definitionKey);
            List<String> parameterKeys = getParameterKey(method.getParameters(), joinPoint.getArgs());
            keyList.addAll(parameterKeys);
            String prefix = lock.prefix();
            if (!prefix.endsWith(":")) {
                prefix += ":";
            }
            return MAD_PREFIX + prefix + StringUtils.collectionToDelimitedString(keyList, "", "-", "");
        }
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 如果申明为借口，那么从实际类中获取参数
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }

    /**
     * 根据SPEL表达式获取 KEY-value
     */
    private String getSpelDefinitionKey(String definitionKey, Method method, Object[] parameterValues) {
        String key = "";
        if (!ObjectUtils.isEmpty(definitionKey)) {
            EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
            Object objKey = parser.parseExpression(definitionKey).getValue(context);
            key = ObjectUtils.nullSafeToString(objKey);
        }
        return key;
    }

    /**
     * 获取 DLockKey(含SPEL表达式) 关键字的value
     */
    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(DLockKey.class) != null) {
                DLockKey keyAnnotation = parameters[i].getAnnotation(DLockKey.class);
                if (keyAnnotation.value().isEmpty()) {
                    Object parameterValue = parameterValues[i];
                    parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                    Object key = parser.parseExpression(keyAnnotation.value()).getValue(context);
                    parameterKey.add(ObjectUtils.nullSafeToString(key));
                }
            }
        }
        return parameterKey;
    }
}
