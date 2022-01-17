package io.hotcloud.message.server.websocket;

import com.github.javafaker.Faker;
import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBody;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMessageApplicationTest.class
)
@Slf4j
@ActiveProfiles("websocket-message-integration-test")
public class WebSocketMessageSubscribeClientIT {

    private final Faker faker = new Faker();
    AtomicInteger count = new AtomicInteger(0);
    @Qualifier("webSocketMessageBroadcaster")//for eliminate compiler errors only
    @Autowired
    private MessageBroadcaster messageBroadcaster;

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
                    connected.set(false);
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
