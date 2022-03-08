package io.hotcloud.kubernetes.server;


import io.hotcloud.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class WebResponse {
    private WebResponse() {
    }

    public static <T> ResponseEntity<Result<T>> none() {
        return ResponseEntity.ok(Result.none());
    }

    public static <T> ResponseEntity<Result<T>> ok(T data) {
        return ok(HttpStatus.OK, data);
    }

    public static <T> ResponseEntity<Result<T>> ok(HttpStatus status, T data) {
        return ok("success", status, data);
    }

    public static <T> ResponseEntity<Result<T>> ok(String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(Result.ok(status.value(), message, data));
    }

    public static <T> ResponseEntity<Result<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(HttpStatus.CREATED.value(), data));
    }

    public static <T> ResponseEntity<Result<T>> created() {
        return created(null);
    }

    public static <T> ResponseEntity<Result<T>> accepted(T data) {
        return ResponseEntity.accepted().body(Result.ok(HttpStatus.ACCEPTED.value(), data));
    }

    public static <T> ResponseEntity<Result<T>> accepted() {
        return accepted(null);
    }

}
