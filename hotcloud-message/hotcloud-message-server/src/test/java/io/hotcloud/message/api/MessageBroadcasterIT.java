package io.hotcloud.message.api;

import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = HotCloudMessageApplicationTest.class
)
public class MessageBroadcasterIT {

    @Autowired
    private MessageBroadcaster messageBroadcaster;

    @Test
    public void broadcast() {
        MessageBody body = MessageBody.of("Hi", "RabbitMQ");
        Message<MessageBody> message = Message.of(body, Message.Level.INFO, "Demo desc", "Broadcast message");
        messageBroadcaster.broadcast(message);
    }


}
