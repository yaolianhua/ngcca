package io.hotCloud.core.common;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Result<T> {

    private int status;
    private String message;
    private T data;

    public Result(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
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
