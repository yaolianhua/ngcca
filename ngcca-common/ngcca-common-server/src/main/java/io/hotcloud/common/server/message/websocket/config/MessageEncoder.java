package io.hotcloud.common.server.message.websocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.api.message.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public class MessageEncoder implements Encoder.Text<Message<?>> {

    private ObjectMapper objectMapper;

    @Override
    public String encode(Message<?> message) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new EncodeException("Json process error {}", e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        Map<String, Object> userProperties = endpointConfig.getUserProperties();
        objectMapper = ((ObjectMapper) userProperties.get(ObjectMapper.class.getName()));
    }

    @Override
    public void destroy() {

    }
}
