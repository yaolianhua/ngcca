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

    public static final String LISTENER_ENABLED = "message.rabbitmq.listener.enabled";
    private Listener listener = new Listener();
    private String host;
    private Integer port;

    private String username;
    private String password;

    @PostConstruct
    public void print() {
        log.info("【Load RabbitMQ Configuration. url {} 】", String.format("amqp://%s:%s@%s:%s", username, password, host, port));
        if (!this.listener.isEnabled()) {
            log.warn("【Event listener is disabled, all events will be ignored】");
        }
    }

    @Data
    public static class Listener {
        private boolean enabled;
    }
}
