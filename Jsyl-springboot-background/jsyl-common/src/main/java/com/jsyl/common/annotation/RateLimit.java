package com.jsyl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 结合 Redis 实现接口限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流 key 前缀
     */
    String key() default "rate_limit:";

    /**
     * 限流时间窗口（秒）
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 10;

    /**
     * 限流提示消息
     */
    String message() default "操作过于频繁，请稍后再试";
}
