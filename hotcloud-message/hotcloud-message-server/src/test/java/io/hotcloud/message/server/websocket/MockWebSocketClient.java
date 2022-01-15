package io.hotcloud.message.server.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class MockWebSocketClient extends WebSocketClient {

    public MockWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("client opened: {}", serverHandshake);
    }

    @Override
    public void onMessage(String message) {
        log.info("client received message: {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("client closed: code={}, reason={}, remote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.info("client onError: {}", ex.getCause(), ex);
    }

    @Override
    public void connect() {
        super.connect();
        log.info("client connected");
    }
}
