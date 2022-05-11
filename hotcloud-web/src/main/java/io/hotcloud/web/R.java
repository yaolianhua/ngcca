package io.hotcloud.web;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class R<T> {

    private int code;
    private String message;
    private T data;

    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> error(int status, String message) {
        return new R<>(status, message, null);
    }

    public static <T> R<T> error(int status, String message, T data) {
        return new R<>(status, message, data);
    }

    public static <T> R<T> none() {
        return new R<>(200, "success", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> ok(int status) {
        return new R<>(status, "success", null);
    }

    public static <T> R<T> ok(int status, String message, T data) {
        return new R<>(status, message, data);
    }

    public static <T> R<T> ok(int status, T data) {
        return new R<>(status, "success", data);
    }
}
