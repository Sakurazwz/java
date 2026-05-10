package com.example.redisdemo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * 使用 Redis + AOP 实现接口级别的限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 key 前缀
     */
    String key() default "rate_limit";

    /**
     * 时间窗口内允许的最大请求数
     */
    int limit() default 10;

    /**
     * 时间窗口大小
     */
    int window() default 60;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}
