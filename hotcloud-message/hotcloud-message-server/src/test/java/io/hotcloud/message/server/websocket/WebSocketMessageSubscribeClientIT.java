package io.hotcloud.message.server.websocket;

import com.github.javafaker.Faker;
import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBody;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import lombok.extern.slf4j.Slf4j;
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
     * run this main method after server was started
     * {@link WebSocketMessageSubscribeClientIT#broadcast()}
     */
    public static void main(String[] args) throws URISyntaxException {
        MockWebSocketClient mockWebSocketClient = new MockWebSocketClient(new URI("ws://localhost:8080/pub"));
        mockWebSocketClient.connect();
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
