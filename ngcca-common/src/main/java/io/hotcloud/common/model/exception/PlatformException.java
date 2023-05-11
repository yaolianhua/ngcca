package io.hotcloud.common.model.exception;

public class PlatformException extends RuntimeException {

    private int code;

    public PlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatformException(String message) {
        this(message, 403);
    }

    public PlatformException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
