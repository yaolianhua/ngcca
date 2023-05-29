package io.hotcloud.service.module;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.task-execution-and-scheduling">Task Execution and Scheduling</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
@EnableScheduling
public class SystemAsyncConfiguration {
    @PostConstruct
    public void print() {
        Log.info(this, null, Event.START, "enable spring async scheduling configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(TaskExecutorBuilder taskExecutorBuilder) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = taskExecutorBuilder.threadNamePrefix("common-").build();
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExecutorService executorService(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean
    public Executor executor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor;
    }
}
