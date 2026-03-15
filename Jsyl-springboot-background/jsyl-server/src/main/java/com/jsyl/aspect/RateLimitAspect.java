package com.jsyl.aspect;

import com.jsyl.annotation.RateLimit;
import com.jsyl.context.BaseContext;
import com.jsyl.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 限流切面
 * 使用 Redis INCR 实现滑动窗口限流
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.jsyl.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        // 获取用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            // 未登录用户，使用 IP 作为限流 key
            log.warn("限流检查：用户未登录");
            return joinPoint.proceed();
        }

        // 构建 Redis key
        String key = rateLimit.key() + userId + ":" + method.getName();

        try {
            // 使用 Redis INCR 实现限流
            Long count = redisTemplate.opsForValue().increment(key);

            if (count == 1) {
                // 首次访问，设置过期时间
                redisTemplate.expire(key, rateLimit.time(), java.util.concurrent.TimeUnit.SECONDS);
            }

            if (count > rateLimit.count()) {
                // 超过限流次数
                log.warn("限流触发：用户 {}, 方法 {}, 当前次数 {}, 限流次数 {}",
                        userId, method.getName(), count, rateLimit.count());

                // 返回限流提示
                return Result.error(rateLimit.message());
            }

            log.debug("限流检查通过：用户 {}, 方法 {}, 当前次数 {}", userId, method.getName(), count);
            return joinPoint.proceed();

        } catch (Exception e) {
            log.error("限流检查异常：{}", e.getMessage());
            // 限流异常时，放行请求（降级处理）
            return joinPoint.proceed();
        }
    }
}
