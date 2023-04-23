package io.hotcloud.server;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.task-execution-and-scheduling">Task Execution and Scheduling</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableRabbit
@EnableAsync
@EnableScheduling
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
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
