package io.hotcloud.common.server.message;

import io.hotcloud.common.autoconfigure.RabbitmqConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class RabbitmqMessageSubscribeListener {

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = RabbitmqConfiguration.QUEUE_SUBSCRIBE_MESSAGE),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = RabbitmqConfiguration.EXCHANGE_FANOUT_BROADCAST_MESSAGE)
                    )
            }
    )
    public void subscribe(String message) throws InterruptedException {
        RabbitmqMessageBroadcasterIT.COUNT_DOWN_LATCH.await();
        log.info("Received message: \n {}", message);
    }
}
