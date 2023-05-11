package io.hotcloud.common.model.exception;

public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceConflictException(String message) {
        super(message);
    }
}
