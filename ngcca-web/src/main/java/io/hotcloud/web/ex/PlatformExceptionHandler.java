package io.hotcloud.web.ex;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.common.model.exception.ResourceConflictException;
import io.hotcloud.common.model.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(-1)
public class PlatformExceptionHandler {

    private String formatMessage(String uri, String message) {
        return String.format("request %s error: %s", uri, message);
    }

    @ExceptionHandler(value = PlatformException.class)
    public ResponseEntity<Result<Void>> handle(PlatformException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(ex.getCode(), ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handle(ResourceNotFoundException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(404, ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(value = ResourceConflictException.class)
    public ResponseEntity<Result<Void>> handle(ResourceConflictException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(409, ex.getMessage());
        Log.error(this, null, Event.EXCEPTION, formatMessage(request.getRequestURI(), ex.getMessage()));
        return ResponseEntity.status(409).body(error);
    }
}
