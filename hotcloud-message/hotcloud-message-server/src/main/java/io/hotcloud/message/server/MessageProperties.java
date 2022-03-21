package io.hotcloud.message.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;


/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "message")
@Data
@Slf4j
public class MessageProperties {

    public static final String PROPERTIES_TYPE_NAME = "message.type";
    public static final String WEBSOCKET = "websocket";
    public static final String RABBITMQ = "rabbitmq";
    /**
     * message service implementation type. default {@code websocket}, optional type:
     * <ul>
     * <li> websocket
     * <li> rabbitmq
     * </ul>
     */
    private Type type = Type.websocket;

    @PostConstruct
    public void print() {

        if (Type.websocket.equals(type)) {
            log.info("【Load Message Service Configuration. implementation using WebSocket】");
        } else if (Type.rabbitmq.equals(type)) {
            log.info("【Load Message Service Configuration. implementation using RabbitMQ】");
        }

    }

    public enum Type {
        //
        websocket, rabbitmq
    }

}
