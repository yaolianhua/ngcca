package io.hotcloud.common.model.exception;

public class NGCCAPlatformException extends RuntimeException {

    private int code;

    public NGCCAPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public NGCCAPlatformException(String message) {
        this(message, 403);
    }

    public NGCCAPlatformException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
