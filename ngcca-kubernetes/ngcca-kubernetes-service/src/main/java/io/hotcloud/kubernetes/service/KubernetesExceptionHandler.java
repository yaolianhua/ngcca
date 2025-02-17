package io.hotcloud.kubernetes.service;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.kubernetes.client.openapi.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.error.YAMLException;


@RestControllerAdvice
@Order(0)
public class KubernetesExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<String> handle(ApiException ex, HttpServletRequest request) {
        String message;
        String msg = ex.getMessage();
        if (StringUtils.hasText(msg)) {
            message = msg;
        } else {
            message = ex.getResponseBody();
        }

        Log.error(this, null, Event.EXCEPTION, ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(message);
    }

    @ExceptionHandler(value = YAMLException.class)
    public ResponseEntity<String> handle(YAMLException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(value = KubernetesClientException.class)
    public ResponseEntity<String> handle(KubernetesClientException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
    }

}
