package io.hotcloud.service.message;

import com.github.javafaker.Faker;
import io.hotcloud.common.model.Message;
import io.hotcloud.service.message.ws.WebSocketMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Deprecated(forRemoval = true)
@Slf4j
public class WebSocketMessageSubscribeClientIT {

    private final Faker faker = new Faker();
    AtomicInteger count = new AtomicInteger(0);
    @Autowired
    private WebSocketMessageBroadcaster messageBroadcaster;

    /**
     * {@link WebSocketMessageSubscribeClientIT#broadcast()}
     */
    static AtomicReference<Boolean> connected = new AtomicReference<>(false);

    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        while (!connected.get()) {
            TimeUnit.SECONDS.sleep(3);
            new WebSocketClient(new URI("ws://localhost:8078/pub")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("client opened: {}", serverHandshake);
                    connected.set(true);
                }

                @Override
                public void onMessage(String message) {
                    log.info("client received message: {}", message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("client connect failure: code={}, reason={}, remote={}", code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {

                }
            }.connect();
        }

    }

    @Test
    public void broadcast() throws InterruptedException {
        while (count.incrementAndGet() < 10) {

            MessageBody body = MessageBody.of(faker.name().fullName(), faker.address().streetAddress());
            Message<MessageBody> message = Message.of(body, Message.Level.INFO, faker.chuckNorris().fact(), "Broadcast message");
            messageBroadcaster.broadcast(message);
            TimeUnit.SECONDS.sleep(2);
        }

    }

}
