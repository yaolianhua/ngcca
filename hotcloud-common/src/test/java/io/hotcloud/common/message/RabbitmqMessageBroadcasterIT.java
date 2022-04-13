package io.hotcloud.common.message;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMessageApplicationTest.class
)
@Import(RabbitmqMessageSubscribeListener.class)
@ActiveProfiles("rabbitmq-message-integration-test")
@Slf4j
public class RabbitmqMessageBroadcasterIT {

    public final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(5);
    private final Faker faker = new Faker();
    @Qualifier("rabbitmqMessageBroadcaster")//for eliminate compiler errors only
    @Autowired
    private MessageBroadcaster messageBroadcaster;

    @Test
    public void broadcast() throws InterruptedException {

        while (COUNT_DOWN_LATCH.getCount() > 0) {
            MessageBody body = MessageBody.of(faker.name().fullName(), faker.address().streetAddress());
            Message<MessageBody> message = Message.of(body, Message.Level.INFO, faker.chuckNorris().fact(), "Broadcast message");
            messageBroadcaster.broadcast(message);
            COUNT_DOWN_LATCH.countDown();
            TimeUnit.SECONDS.sleep(2);
        }


    }

}
