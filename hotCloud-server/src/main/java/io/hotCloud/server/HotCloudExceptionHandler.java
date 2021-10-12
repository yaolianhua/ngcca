package io.hotCloud.server;

import io.hotCloud.core.common.HotCloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorWebResult> handle(HotCloudException ex, HttpServletRequest request){
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.FORBIDDEN, request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
