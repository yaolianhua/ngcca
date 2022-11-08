package io.hotcloud.common.api.core.message;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "message")
@Data
@Slf4j
@Properties(prefix = "message")
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
    @Properties(prefix = "rabbitmq")
    private RabbitmqProperties rabbitmq;

    public enum Type {
        //
        websocket, rabbitmq
    }

    @Data
    public static class RabbitmqProperties {

        private String host;
        private Integer port;
        private String username;
        private String password;

    }

}
