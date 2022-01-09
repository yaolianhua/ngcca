package io.hotcloud.message.server.rabbitmq;

import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = HotCloudMessageApplicationTest.class
)
@Import(RabbitmqMessageSubscribeListener.class)
@Slf4j
public class MessageSubscribeListenerIT {

    @Test
    public void context() throws InterruptedException {
        log.info("Waiting for receive message ...");
        Thread.sleep(60 * 1000);
    }
}
