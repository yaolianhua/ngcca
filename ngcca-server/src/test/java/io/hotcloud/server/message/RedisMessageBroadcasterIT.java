package io.hotcloud.server.message;

import com.github.javafaker.Faker;
import io.hotcloud.common.model.Message;
import io.hotcloud.server.NgccaCoreServerApplication;
import io.hotcloud.service.message.RedisMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.hotcloud.common.model.CommonConstant.MESSAGE_QUEUE_TEST;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgccaCoreServerApplication.class
)
@Import(RedisMessageTestObserver.class)
@ActiveProfiles("test")
@Slf4j
public class RedisMessageBroadcasterIT {

    public final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(5);
    private final Faker faker = new Faker();
    @Autowired
    private RedisMessageBroadcaster messageBroadcaster;

    @Test
    public void broadcast() throws InterruptedException {

        while (COUNT_DOWN_LATCH.getCount() > 0) {

            MessageBody body = MessageBody.of(faker.name().fullName(), faker.address().streetAddress());

            Message<?> message = Message.of(body, Message.Level.INFO, faker.chuckNorris().fact(), "Broadcast message");

            messageBroadcaster.broadcast(MESSAGE_QUEUE_TEST, message);

            COUNT_DOWN_LATCH.countDown();
        }

        //
        TimeUnit.SECONDS.sleep(5);
    }

}
