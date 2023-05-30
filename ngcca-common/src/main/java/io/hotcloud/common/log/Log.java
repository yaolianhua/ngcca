package io.hotcloud.common.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hotcloud.common.model.exception.PlatformException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Log implements Serializable {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Builder.Default
    private Level level = Level.INFO;

    @Builder.Default
    private String type = "server";

    @Builder.Default
    private Event event = Event.NORMAL;

    private String message;

    private Object body;

    private String component;


    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    private static String writeAsString(Log log) {
        try {
            return objectMapper.writeValueAsString(log);
        } catch (JsonProcessingException e) {
            throw new PlatformException(e.getMessage());
        }
    }

    public static void info(Object component, Object body, String message){
        info(component, body, Event.NORMAL, message);
    }
    public static void info(Object component, Object body, Event event, String message){
        Log msg = Log.builder()
                .level(Level.INFO)
                .event(event)
                .message(message)
                .body(body)
                .component(component == null ? null : component.getClass().getName())
                .build();
        log.info(writeAsString(msg));
    }

    public static void debug(Object component, Object body, String message){
        debug(component, body, Event.NORMAL, message);
    }
    public static void debug(Object component, Object body, Event event, String message){
        Log msg = Log.builder()
                .level(Level.DEBUG)
                .event(event)
                .message(message)
                .body(body)
                .component(component == null ? null : component.getClass().getName())
                .build();
        log.debug(writeAsString(msg));
    }
    public static void warn(Object component, Object body, String message){
        warn(component, body, Event.NORMAL, message);
    }
    public static void warn(Object component, Object body, Event event, String message){
        Log msg = Log.builder()
                .level(Level.WARN)
                .event(event)
                .message(message)
                .body(body)
                .component(component == null ? null : component.getClass().getName())
                .build();
        log.warn(writeAsString(msg));
    }

    public static void error(Object component, Object body, String message){
        error(component, body, Event.NORMAL, message);
    }
    public static void error(Object component, Object body, Event event, String message){
        Log msg = Log.builder()
                .level(Level.ERROR)
                .event(event)
                .message(message)
                .body(body)
                .component(component == null ? null : component.getClass().getName())
                .build();
        log.error(writeAsString(msg));
    }

}
