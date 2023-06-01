package io.hotcloud.web.ex;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(-1)
public class JDKNormallyExceptionHandler {

    private String formatMessage(String uri, String message) {
        return String.format("request %s error: %s", uri, message);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handle(IllegalArgumentException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(400, ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Result<Void>> handle(IllegalStateException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(500, ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(value = UnsupportedOperationException.class)
    public ResponseEntity<Result<Void>> handle(UnsupportedOperationException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(500, ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(500).body(error);
    }
}
