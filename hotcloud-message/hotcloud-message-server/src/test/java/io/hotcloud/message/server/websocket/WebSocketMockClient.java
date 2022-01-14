package io.hotcloud.message.server.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class WebSocketMockClient extends WebSocketClient {

    public WebSocketMockClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("WebSocketMockClient onOpen ");
    }

    @Override
    public void onMessage(String message) {
        log.info("WebSocketMockClient onMessage: {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocketMockClient onClose: code={}, reason={}, remote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.info("WebSocketMockClient onError: {}", ex.getCause(), ex);
    }

    @Override
    public void connect() {
        super.connect();
        log.info("WebSocketMockClient connected");
    }
}
