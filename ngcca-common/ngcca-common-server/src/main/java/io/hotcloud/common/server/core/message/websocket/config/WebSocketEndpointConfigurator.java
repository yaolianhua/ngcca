package io.hotcloud.common.server.core.message.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class WebSocketEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);

        Map<String, Object> userProperties = sec.getUserProperties();

        ObjectMapper objectMapper = new ObjectMapper();
        userProperties.put(ObjectMapper.class.getName(), objectMapper);

        internalModifyHandshake(sec, request, response);
    }

    abstract protected void internalModifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response);
}
