package io.hotcloud.common.autoconfigure;

import io.hotcloud.common.autoconfigure.cache.RedisConfiguration;
import io.hotcloud.common.autoconfigure.minio.MinioConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        RegistryProperties.class,
        RabbitmqProperties.class
})
@Import({
        RedisConfiguration.class,
        MinioConfiguration.class
})
@EnableRabbit
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}
