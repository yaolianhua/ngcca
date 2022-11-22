package io.hotcloud.common.api.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@AllArgsConstructor
public class MessageBody implements Serializable {

    private String name;
    private String value;

    private MessageBodyInner bodyInner;

    public static MessageBody of(String name, String value) {
        return new MessageBody(name, value, new MessageBodyInner(name, value));
    }

    @Data
    @AllArgsConstructor
    public static class MessageBodyInner implements Serializable {
        private String name;
        private String value;
    }

}
