package io.hotcloud.message.server.rabbitmq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("message.rabbitmq")
@Data
@Slf4j
public class RabbitmqProperties {

    private String host;
    private Integer port;

    private String username;
    private String password;

    @PostConstruct
    public void print() {
        log.info("【Load RabbitMQ Configuration. url: {} 】", String.format("amqp://%s@%s:%s", username, host, port));
    }

}
