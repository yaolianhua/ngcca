package io.hotcloud.kubernetes.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.kubernetes.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KubernetesRabbitmqMessageBroadcaster {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public KubernetesRabbitmqMessageBroadcaster(RabbitTemplate rabbitTemplate,
                                                ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void broadcast(String exchange, Message<T> message) {
        try {
            String content = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(exchange, "", content);
            log.debug("Rabbitmq broadcast message: \n {}", content);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
