package io.hotcloud.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.exception.HotCloudException;
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
    private long timestamp = System.currentTimeMillis();

    @Builder.Default
    private Level level = Level.INFO;

    private String component;

    private String message;

    private static String writeAsString(Log message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new HotCloudException(e.getMessage());
        }
    }

    public static void info(String component, String message) {
        Log msg = Log.builder()
                .component(component)
                .message(message)
                .level(Level.INFO)
                .build();
        log.info(writeAsString(msg));
    }

    public static void debug(String component, String message) {
        Log msg = Log.builder()
                .component(component)
                .message(message)
                .level(Level.DEBUG)
                .build();
        log.debug(writeAsString(msg));
    }

    public static void warn(String component, String message) {
        Log msg = Log.builder()
                .component(component)
                .message(message)
                .level(Level.WARN)
                .build();
        log.warn(writeAsString(msg));
    }

    public static void error(String component, String message) {
        Log msg = Log.builder()
                .component(component)
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
