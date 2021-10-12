package io.hotCloud.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorWebResult {

    private int status;
    private String reason;
    private String path;
    private LocalDateTime timestamp;
    private Object message;

    public static ErrorWebResult error(HttpStatus status, String path, String message) {
        ErrorWebResult errorWebResult = new ErrorWebResult();
        errorWebResult.setStatus(status.value());
        errorWebResult.setReason(status.getReasonPhrase());
        errorWebResult.setPath(path);
        errorWebResult.setTimestamp(LocalDateTime.now());
        errorWebResult.setMessage(message);
        return errorWebResult;
    }

    public static ErrorWebResult error(HttpStatus status, String path, Object body) {
        ErrorWebResult errorWebResult = new ErrorWebResult();
        errorWebResult.setStatus(status.value());
        errorWebResult.setReason(status.getReasonPhrase());
        errorWebResult.setPath(path);
        errorWebResult.setTimestamp(LocalDateTime.now());
        errorWebResult.setMessage(body);
        return errorWebResult;
    }

}
