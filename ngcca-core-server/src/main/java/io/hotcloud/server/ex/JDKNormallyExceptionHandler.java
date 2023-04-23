package io.hotcloud.server.ex;

import io.hotcloud.common.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestControllerAdvice
@Slf4j
@Order(-1)
public class JDKNormallyExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handle(IllegalArgumentException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(400, ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Result<Void>> handle(IllegalStateException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(500, ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(value = UnsupportedOperationException.class)
    public ResponseEntity<Result<Void>> handle(UnsupportedOperationException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(500, ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
