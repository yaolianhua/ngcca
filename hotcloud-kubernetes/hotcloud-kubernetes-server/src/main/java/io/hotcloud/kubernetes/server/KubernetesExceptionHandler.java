package io.hotcloud.kubernetes.server;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.hotcloud.common.Result;
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
    public ResponseEntity<Result<Void>> handle(ApiException ex, HttpServletRequest request) {
        String message;
        String msg = ex.getMessage();
        if (StringUtils.hasText(msg)) {
            message = msg;
        } else {
            message = ex.getResponseBody();
        }
        Result<Void> error = Result.error(ex.getCode(), message);
        log.error("{}", message, ex);
        return ResponseEntity.status(ex.getCode()).body(error);
    }

    @ExceptionHandler(value = YAMLException.class)
    public ResponseEntity<Result<Void>> handle(YAMLException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        log.error("{}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(value = KubernetesClientException.class)
    public ResponseEntity<Result<Void>> handle(KubernetesClientException ex, HttpServletRequest request) {
        Result<Void> error = Result.error(ex.getCode(), ex.getMessage());
        log.error("{}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getCode()).body(error);
    }

}
