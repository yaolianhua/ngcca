package io.hotcloud.common.message.websocket;


import io.hotcloud.common.message.websocket.config.WebSocketEndpointConfigurator;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author yaolianhua789@gmail.com
 **/
public class MessageBroadcastEndpointConfigurator extends WebSocketEndpointConfigurator {

    @Override
    protected void internalModifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

    }
}
