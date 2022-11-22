package io.hotcloud.common.api.core.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class RabbitmqMessageSubscribeListener {

    @Bean
    public org.springframework.amqp.core.Queue queue() {
        return QueueBuilder.durable("queue.subscribe.message").build();
    }

    @Bean
    public org.springframework.amqp.core.Exchange exchange() {
        return ExchangeBuilder.fanoutExchange("exchange.subscribe.message").durable(true).build();
    }


    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = "queue.subscribe.message"),
                            exchange = @Exchange(type = ExchangeTypes.FANOUT, value = "exchange.subscribe.message")
                    )
            }
    )
    public void subscribe(String message) throws InterruptedException {
        RabbitmqMessageBroadcasterIT.COUNT_DOWN_LATCH.await();
        log.info("Received message: \n {}", message);
    }
}
