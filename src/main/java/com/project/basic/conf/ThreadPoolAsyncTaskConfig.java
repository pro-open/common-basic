package com.project.basic.conf;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@EnableAsync
@Configuration
@EnableScheduling
public class ThreadPoolAsyncTaskConfig implements AsyncConfigurer,SchedulingConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolAsyncTaskConfig.class);

    @Value("${spring.task.corePoolSize}")
    private int corePoolSize;
    @Value("${spring.task.maxPoolSize}")
    private int maxPoolSize;
    @Value("${spring.task.queueCapacity}")
    private int queueCapacity;
    @Value("${spring.task.keepAliveSeconds}")
    private int keepAliveSeconds;
    @Value("${spring.task.threadNamePrefix}")
    private String threadNamePrefix;
    
    @Value("${spring.scheduler.poolSize}")
    private int poolSize;
    @Value("${spring.scheduler.awaitTerminationSeconds}")
    private int awaitTerminationSeconds;
    @Value("${spring.scheduler.threadNamePrefix}")
    private String schedulerNamePrefix;
    @Value("${spring.scheduler.waitForTasksToCompleteOnShutdown}")
    private boolean waitForTasksToCompleteOnShutdown;
    
    @Override
    public Executor getAsyncExecutor() {
        return initThreadPoolTaskExecutor();
    }
    
    @Bean(destroyMethod="shutdown")
    public Executor initThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        LOGGER.debug("初始化threadPoolTaskExecutor完成了corePoolSize={},maxPoolSize={}",corePoolSize,maxPoolSize);
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                LOGGER.error("AsyncUncaughtExceptionHandler处理异常信息为:{}",ex.getMessage());
            }
        };
    }
    
    @Bean
    public ThreadPoolTaskScheduler initThreadPoolTaskScheduler () {
        ThreadPoolTaskScheduler  threadPoolTaskScheduler = new ThreadPoolTaskScheduler ();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        threadPoolTaskScheduler.setThreadNamePrefix(schedulerNamePrefix);
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        threadPoolTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskScheduler.initialize();
        LOGGER.debug("初始化threadPoolTaskScheduler完成了corePoolSize={},maxPoolSize={}",corePoolSize,maxPoolSize);
        return threadPoolTaskScheduler;
    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        TaskScheduler scheduler = this.initThreadPoolTaskScheduler();
        registrar.setTaskScheduler(scheduler);
    }
    
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

}