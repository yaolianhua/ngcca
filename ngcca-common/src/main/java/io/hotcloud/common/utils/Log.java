package io.hotcloud.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Log implements Serializable {

    @Builder.Default
    private String type = "server";

    @Builder.Default
    private String event = "normal";

    private String message;


    @Builder.Default
    private Level level = Level.INFO;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    private String component;

    private static String writeAsString(Log message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException(e.getMessage());
        }
    }

    public static void info(String component, String message) {
        info(component, "normal", message);
    }

    public static void info(String component, String event, String message) {
        Log msg = Log.builder()
                .component(component)
                .message(message)
                .event(event)
                .level(Level.INFO)
                .build();
        log.info(writeAsString(msg));
    }

    public static void debug(String component, String message) {
        debug(component, "normal", message);
    }

    public static void debug(String component, String event, String message) {
        Log msg = Log.builder()
                .component(component)
                .event(event)
                .message(message)
                .level(Level.DEBUG)
                .build();
        log.debug(writeAsString(msg));
    }

    public static void warn(String component, String message) {
        warn(component, "normal", message);
    }

    public static void warn(String component, String event, String message) {
        Log msg = Log.builder()
                .component(component)
                .event(event)
                .message(message)
                .level(Level.WARN)
                .build();
        log.warn(writeAsString(msg));
    }

    public static void error(String component, String message) {
        error(component, "normal", message);
    }

    public static void error(String component, String event, String message) {
        Log msg = Log.builder()
                .component(component)
                .event(event)
                .message(message)
                .level(Level.ERROR)
                .build();
        log.error(writeAsString(msg));
    }

    public enum Level {
        //
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
