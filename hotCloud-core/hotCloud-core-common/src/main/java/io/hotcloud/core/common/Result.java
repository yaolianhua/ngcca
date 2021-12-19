package io.hotcloud.core.common;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        timestamp = LocalDateTime.now();
    }

    public static <T> Result<T> error(int status, String message) {
        return new Result<>(status, message, null);
    }

    public static <T> Result<T> none() {
        return new Result<>(200, null, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, null, data);
    }

    public static <T> Result<T> ok(int status) {
        return new Result<>(status, "success", null);
    }

    public static <T> Result<T> ok(int status, String message, T data) {
        return new Result<>(status, message, data);
    }

    public static <T> Result<T> ok(int status, T data) {
        return new Result<>(status, null, data);
    }
}
