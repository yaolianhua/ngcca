package io.hotcloud.common.server.exception.handler;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.exception.NGCCAResourceConflictException;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
@Order(-1)
public class NGCCAExceptionHandler {

    @ExceptionHandler(value = NGCCACommonException.class)
    public ResponseEntity<Result<Void>> handle(NGCCACommonException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(ex.getCode(), ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = NGCCAResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handle(NGCCAResourceNotFoundException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(404, ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(value = NGCCAResourceConflictException.class)
    public ResponseEntity<Result<Void>> handle(NGCCAResourceConflictException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(409, ex.getMessage());
        log.error("request '{}' error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(409).body(error);
    }
}
