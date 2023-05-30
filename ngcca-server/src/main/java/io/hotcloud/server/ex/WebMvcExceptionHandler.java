package io.hotcloud.server.ex;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
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

import java.util.Arrays;

@RestControllerAdvice
@Order(0)
public class WebMvcExceptionHandler {


    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Result<Void>> handle(HttpMessageNotWritableException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Internal server error '%s'", ex.getMessage()));
        Result<Void> error = Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<Void>> handle(MissingServletRequestParameterException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Required request parameter '%s' for '%s'", ex.getParameterName(), request.getRequestURI()));
        Result<Void> error = Result.error(HttpStatus.BAD_REQUEST.value(), String.format("Required request parameter '%s'", ex.getParameterName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<Void>> handle(ServletRequestBindingException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Parameter exception '%s' for '%s'", ex.getMessage(), request.getRequestURI()));
        Result<Void> error = Result.error(HttpStatus.BAD_REQUEST.value(), "Request parameter exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<Void>> handle(MethodArgumentNotValidException ex, HttpServletRequest request) {

        StringBuffer message = new StringBuffer();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            message.append(fieldError.getDefaultMessage());
            message.append("; ");
        });

        Result<Void> error = Result.error(HttpStatus.BAD_REQUEST.value(), message.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<Void>> handle(BindException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Request parameter error for '%s'", request.getRequestURI()));
        FieldError fieldError = ex.getFieldError();
        String message = "Request parameter error";
        if (fieldError != null) {
            message = String.format("Request parameter '%s' error, message: '%s'", fieldError.getField(), fieldError.getDefaultMessage());
        }

        Result<Void> error = Result.error(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        MethodParameter methodParameter = ex.getParameter();
        if (methodParameter.hasParameterAnnotation(PathVariable.class)) {
            return ResponseEntity.notFound().build();
        } else if (methodParameter.hasParameterAnnotation(RequestParam.class)) {
            return getResponseEntity(ex);
        } else {
            return getResponseEntity(ex);
        }
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(MethodArgumentTypeMismatchException ex) {
        Log.error(this, null, Event.EXCEPTION, String.format("parameter error: [name=%s, value=%s, message=\"%s\"]", ex.getName(), ex.getValue(), ex.getMessage()));
        Result<Void> result = Result.error(HttpStatus.BAD_REQUEST.value(), String.format("parameter error: [%s=%s]", ex.getName(), ex.getValue()));
        return ResponseEntity.badRequest().body(result);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Result<Void>> handle(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Not supported method '%s' for '%s'", ex.getMethod(), request.getRequestURI()));
        Result<Void> error = Result.error(HttpStatus.METHOD_NOT_ALLOWED.value(), String.format("Not supported method '%s' required method is '%s'", ex.getMethod(), Arrays.toString(ex.getSupportedMethods())));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<Void>> handle(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Log.error(this, null, Event.EXCEPTION, String.format("Request error '%s' for '%s'", ex.getMessage(), request.getRequestURI()));
        Result<Void> error = Result.error(HttpStatus.BAD_REQUEST.value(), String.format("Request error '%s' for '%s'", ex.getMessage(), request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
