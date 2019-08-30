package com.cael.omr.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Getter
public class ThreadConfig {

    @Value("${thread.corePoolSize}")
    private int corePoolSize;

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;

    @Value("${thread.queueCapacity}")
    private int queueCapacity;

    @Value("${debug:''}")
    private String debug;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ThreadPoolTaskExecutor");
        executor.setWaitForTasksToCompleteOnShutdown(false);
//        executor.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
        executor.initialize();
        return executor;
    }

//    @Bean(name = "ConcurrentTaskExecutor")
//    public TaskExecutor concurrentTaskExecutor () {
//        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3));
//    }

//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new AsyncExceptionHandler();
//    }
}
