package io.hotcloud.common.server.message.websocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.server.message.Message;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class MessageDecoder implements Decoder.Text<Message<?>> {

    private ObjectMapper objectMapper;

    @Override
    public Message<?> decode(String s) throws DecodeException {
        try {
            return objectMapper.readValue(s, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new DecodeException("Json process error {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
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
