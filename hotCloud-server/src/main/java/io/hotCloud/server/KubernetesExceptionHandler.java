package io.hotCloud.server;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.error.YAMLException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestControllerAdvice
@Slf4j
@Order(0)
public class KubernetesExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorWebResult> handle(ApiException ex, HttpServletRequest request){
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.FORBIDDEN, request.getRequestURI(), ex.getResponseBody());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(value = YAMLException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorWebResult> handle(YAMLException ex, HttpServletRequest request){
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.FORBIDDEN, request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
