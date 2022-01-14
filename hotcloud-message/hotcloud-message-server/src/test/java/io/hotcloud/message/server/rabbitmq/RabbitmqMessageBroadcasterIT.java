package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBody;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMessageApplicationTest.class
)
public class RabbitmqMessageBroadcasterIT {

    @Autowired
    private MessageBroadcaster messageBroadcaster;

    @Test
    public void broadcast() throws InterruptedException {
        TimeUnit.SECONDS.sleep(15);
        MessageBody body = MessageBody.of("Hi", "Broadcast Message");
        Message<MessageBody> message = Message.of(body, Message.Level.INFO, "Demo desc", "Broadcast message");
        messageBroadcaster.broadcast(message);
    }


}
