package io.hotcloud.common.server.exception.handler;

import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.exception.HotCloudResourceConflictException;
import io.hotcloud.common.api.exception.HotCloudResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestControllerAdvice
@Slf4j
@Order(-1)
public class HotCloudExceptionHandler {

    @ExceptionHandler(value = HotCloudException.class)
    public ResponseEntity<Result<Void>> handle(HotCloudException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = HotCloudResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handle(HotCloudResourceNotFoundException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(404, ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(value = HotCloudResourceConflictException.class)
    public ResponseEntity<Result<Void>> handle(HotCloudResourceConflictException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(409, ex.getMessage());
        return ResponseEntity.status(409).body(error);
    }
}
