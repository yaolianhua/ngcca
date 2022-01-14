package io.hotcloud.message.server.websocket;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBody;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMessageApplicationTest.class
)
@Slf4j
public class WebSocketMessageSubscribeClientIT {

    @Autowired
    private MessageBroadcaster messageBroadcaster;

    @Before
    public void broadcast() throws InterruptedException {
        MessageBody body = MessageBody.of("Hi", "Broadcast Message");
        Message<MessageBody> message = Message.of(body, Message.Level.INFO, "Demo desc", "Broadcast message");
        messageBroadcaster.broadcast(message);

    }

    @Test
    public void subscribe() throws URISyntaxException {
        WebSocketMockClient webSocketMockClient = new WebSocketMockClient(new URI("ws://localhost:8080/pub"));
        webSocketMockClient.connect();

    }


}
