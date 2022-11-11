package io.hotcloud.common.model.exception;

public class NGCCACommonException extends RuntimeException {

    private int code;

    public NGCCACommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public NGCCACommonException(String message) {
        this(message, 403);
    }

    public NGCCACommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
