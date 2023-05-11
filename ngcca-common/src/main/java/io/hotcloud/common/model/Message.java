package io.hotcloud.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message<T> implements Serializable {

    private transient T data;
    private Level level;
    private String description;
    private String subject;
    private long timestamp;

    public enum Level {
        //
        DEBUG, INFO, WARN, ERROR
    }

    public Message(T data, Level level, String description, String subject) {
        this.data = data;
        this.level = level;
        this.description = description;
        this.subject = subject;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Message<T> of(T data, Level level, String description, String subject) {
        return new Message<>(data, level, description, subject);
    }

    public static <T> Message<T> of(T data) {
        return new Message<>(data, Level.INFO, null, null);
    }

}
