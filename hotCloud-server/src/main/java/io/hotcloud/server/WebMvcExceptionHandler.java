package io.hotcloud.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestControllerAdvice
@Order(0)
@Slf4j
public class WebMvcExceptionHandler {


    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorWebResult> handle(HttpMessageNotWritableException ex, HttpServletRequest request) {
        log.error("Internal server error '{}'", ex.getMessage(), ex);
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorWebResult> handle(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Required request parameter '{}' for '{}'", ex.getParameterName(), request.getRequestURI(), ex);
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), String.format("Required request parameter '%s'", ex.getParameterName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorWebResult> handle(ServletRequestBindingException ex, HttpServletRequest request) {
        log.warn("Parameter exception '{}' for '{}'",ex.getMessage(), request.getRequestURI(), ex);
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Request parameter exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorWebResult> handle(MethodArgumentNotValidException ex, HttpServletRequest request) {
//        log.warn(ex.getMessage(), ex);

        StringBuffer message = new StringBuffer();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            message.append(fieldError.getDefaultMessage());
            message.append("; ");
        });

        ErrorWebResult error = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), message.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorWebResult> handle(BindException ex, HttpServletRequest request) {
        log.warn("Request parameter error for '{}'", request.getRequestURI(), ex);

        FieldError fieldError = ex.getFieldError();
        String message = "Request parameter error";
        if (fieldError != null) {
            message = String.format("Request parameter '%s' error, message: '%s'", fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorWebResult error = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        MethodParameter methodParameter = ex.getParameter();
        if (methodParameter.hasParameterAnnotation(PathVariable.class)) {
            return ResponseEntity.notFound().build();
        } else if (methodParameter.hasParameterAnnotation(RequestParam.class)) {
            log.warn("parameter error: [name={}, value={}, message=\"{}\"]", ex.getName(), ex.getValue(), ex.getMessage());
            ErrorWebResult result = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), String.format("parameter error: [%s=%s]", ex.getName(), ex.getValue()));
            return ResponseEntity.badRequest().body(result);
        } else {
            log.warn("parameter error: [name={}, value={}, message=\"{}\"]", ex.getName(), ex.getValue(), ex.getMessage());
            ErrorWebResult result = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), String.format("parameter error: [%s=%s]", ex.getName(), ex.getValue()));
            return ResponseEntity.badRequest().body(result);
        }
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorWebResult> handle(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Not supported method '{}' for '{}'", ex.getMethod(), request.getRequestURI(), ex);
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.METHOD_NOT_ALLOWED, request.getRequestURI(), String.format(
                "Not supported method '%s' required method is '%s'", ex.getMethod(), Arrays.toString(ex.getSupportedMethods())));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorWebResult> handle(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Request error '{}' for '{}'", ex.getMessage(), request.getRequestURI(), ex);
        ErrorWebResult error = ErrorWebResult.error(HttpStatus.BAD_REQUEST, request.getRequestURI(), String.format(
                "Request error '%s' for '%s'", ex.getMessage(), request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
