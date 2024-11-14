package io.hhplus.conbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);    // 기본 스레드 풀 크기
        taskExecutor.setMaxPoolSize(20);    // 최대 스레드 풀 크기
        taskExecutor.setQueueCapacity(50);  // 대기 큐 크기
        taskExecutor.setThreadNamePrefix("ASYNC-");
        taskExecutor.initialize();

        return taskExecutor;
    }
}
