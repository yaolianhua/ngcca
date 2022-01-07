package io.hotcloud.message.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Message<T> implements Serializable {

    private T data;
    private Level level = Level.DEBUG;
    private String description;
    private String subject;

    public enum Level {
        //
        DEBUG, INFO, WARN, ERROR
    }
}
