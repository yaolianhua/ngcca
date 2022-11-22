package io.hotcloud.common.server.core.message.websocket.config;

import io.hotcloud.common.server.core.message.rabbitmq.RabbitmqMessageBroadcaster;
import io.hotcloud.common.server.core.message.websocket.WebSocketMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(RabbitmqMessageBroadcaster.class)
@Slf4j
public class WebSocketConfiguration {

    @Bean
    @ConditionalOnBean({
            WebSocketMessageBroadcaster.class
    })
    public ServerEndpointExporter endpointExporter() {
        log.info("【Register websocket endpoint exporter】");
        return new ServerEndpointExporter();
    }
}
