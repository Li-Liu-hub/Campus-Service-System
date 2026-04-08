package com.jsyl.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean("cacheDeleteExecutor")
    public ThreadPoolTaskExecutor cacheDeleteExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);       // 核心线程数
        executor.setMaxPoolSize(5);        // 最大线程数
        executor.setQueueCapacity(100);    // 队列容量
        executor.setThreadNamePrefix("cache-delete-");  // 线程名前缀，方便排查问题
        executor.initialize();
        return executor;
    }
}