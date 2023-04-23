package io.hotcloud.common.model.exception;

public class NGCCAResourceConflictException extends RuntimeException {

    public NGCCAResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public NGCCAResourceConflictException(String message) {
        super(message);
    }
}
