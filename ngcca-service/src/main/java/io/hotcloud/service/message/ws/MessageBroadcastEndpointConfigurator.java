package io.hotcloud.service.message.ws;

import io.hotcloud.service.message.ws.config.WebSocketEndpointConfigurator;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;


/**
 * @author yaolianhua789@gmail.com
 **/
public class MessageBroadcastEndpointConfigurator extends WebSocketEndpointConfigurator {

    @Override
    protected void internalModifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

    }
}
