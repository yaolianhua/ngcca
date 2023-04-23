package io.hotcloud.server.message.ws.config;

import io.hotcloud.server.message.RabbitmqMessageBroadcaster;
import io.hotcloud.server.message.ws.WebSocketMessageBroadcaster;
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
