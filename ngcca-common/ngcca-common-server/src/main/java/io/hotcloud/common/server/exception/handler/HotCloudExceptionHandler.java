package io.hotcloud.common.server.exception.handler;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.exception.NGCCAResourceConflictException;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import io.hotcloud.common.model.utils.Log;
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

    @ExceptionHandler(value = NGCCACommonException.class)
    public ResponseEntity<Result<Void>> handle(NGCCACommonException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = NGCCAResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handle(NGCCAResourceNotFoundException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(404, ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(value = NGCCAResourceConflictException.class)
    public ResponseEntity<Result<Void>> handle(NGCCAResourceConflictException ex, HttpServletRequest request) {
        Log.error(HotCloudExceptionHandler.class.getName(),
                "ExceptionHandler",
                String.format("%s", ex.getMessage()));
        Result<Void> error = Result.error(409, ex.getMessage());
        return ResponseEntity.status(409).body(error);
    }
}
