package io.hotcloud.common.message.websocket.config;

import io.hotcloud.common.message.websocket.WebSocketMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
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
