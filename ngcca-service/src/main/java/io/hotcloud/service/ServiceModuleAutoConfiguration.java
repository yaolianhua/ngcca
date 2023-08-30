package io.hotcloud.service;

import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.buildpack.BuildPackProperties;
import io.hotcloud.service.cache.RedisConfiguration;
import io.hotcloud.service.git.GitProxyProperties;
import io.hotcloud.service.openai.OpenAiConfiguration;
import io.hotcloud.service.registry.SystemRegistryImageProperties;
import io.hotcloud.service.registry.SystemRegistryProperties;
import io.hotcloud.service.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties({
        SystemRegistryImageProperties.class,
        SystemRegistryProperties.class,
        GitProxyProperties.class,
        ApplicationProperties.class,
        BuildPackProperties.class
})
@Import({
        RedisConfiguration.class,
        OpenAiConfiguration.class,
        SecurityConfiguration.class
})
@EnableAsync
@EnableScheduling
public class ServiceModuleAutoConfiguration {


    /**
     * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.task-execution-and-scheduling">Task Execution and Scheduling</a>
     */
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
