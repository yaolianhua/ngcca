package io.hotcloud.common.server;

import io.hotcloud.common.api.Log;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AsyncConfiguration {

    @PostConstruct
    public void print() {
        Log.info(AsyncConfiguration.class.getName(), "【Enable spring async scheduling configuration】");
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(TaskExecutorBuilder taskExecutorBuilder) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = taskExecutorBuilder.threadNamePrefix("hotcloud-").build();
        threadPoolTaskExecutor.initialize();
        Log.info(AsyncConfiguration.class.getName(), "【Initialed ThreadPoolTaskExecutor】");
        return threadPoolTaskExecutor;
    }

    @Bean
    public ExecutorService executorService(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        Log.info(AsyncConfiguration.class.getName(), "【Initialed ExecutorService】");
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }

    @Primary
    @Bean
    public Executor executor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        Log.info(AsyncConfiguration.class.getName(), "【Initialed Executor】");
        return threadPoolTaskExecutor;
    }
}
