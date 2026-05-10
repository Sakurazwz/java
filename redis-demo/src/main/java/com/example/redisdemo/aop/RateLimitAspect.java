package com.example.redisdemo.aop;

import com.example.redisdemo.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于 Redis 滑动窗口计数器实现接口限流
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.example.redisdemo.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit);
        int limit = rateLimit.limit();
        int window = rateLimit.window();
        TimeUnit timeUnit = rateLimit.timeUnit();
        long windowSeconds = timeUnit.toSeconds(window);

        // 使用 Redis 计数器实现固定窗口限流
        Long count = redisTemplate.opsForValue().increment(key);

        // 第一次访问时设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(key, window, timeUnit);
        }

        if (count != null && count > limit) {
            log.warn("接口限流触发: key={}, count={}, limit={}/{}s", key, count, limit, windowSeconds);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, rateLimit.message());
        }

        log.debug("限流计数: key={}, count={}, limit={}", key, count, limit);
        return joinPoint.proceed();
    }

    /**
     * 构建限流 key：
     * 格式: rate_limit:{key}:{method}:{window}s
     */
    private String buildKey(RateLimit rateLimit) {
        long windowSeconds = rateLimit.timeUnit().toSeconds(rateLimit.window());
        return String.format("rate_limit:%s:%s:%ds",
                rateLimit.key(),
                windowSeconds,
                windowSeconds);
    }
}
