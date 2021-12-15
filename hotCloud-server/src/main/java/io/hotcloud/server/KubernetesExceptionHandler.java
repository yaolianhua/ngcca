package io.hotcloud.server;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public ResponseEntity<ErrorWebResult> handle(ApiException ex, HttpServletRequest request) {
        Object message;
        String msg = ex.getMessage();
        if (StringUtils.hasText(msg)) {
            message = msg;
        } else {
            message = ex.getResponseBody();
        }
        ErrorWebResult error = ErrorWebResult.error(ex.getCode(), request.getRequestURI(), message);
        log.error("{}", message, ex);
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = YAMLException.class)
    public ResponseEntity<ErrorWebResult> handle(YAMLException ex, HttpServletRequest request) {
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.FORBIDDEN, request.getRequestURI(), ex.getMessage());
        log.error("{}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(value = KubernetesClientException.class)
    public ResponseEntity<ErrorWebResult> handle(KubernetesClientException ex, HttpServletRequest request) {
        ErrorWebResult error = ErrorWebResult.error(ex.getCode(), request.getRequestURI(), ex.getMessage());
        log.error("{}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getCode()).body(error);
    }

}
