package io.hotcloud.common.exception.handler;

import io.hotcloud.common.Result;
import io.hotcloud.common.exception.HotCloudException;
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

}
