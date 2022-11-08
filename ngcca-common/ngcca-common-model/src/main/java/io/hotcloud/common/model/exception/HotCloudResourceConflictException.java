package io.hotcloud.common.model.exception;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudResourceConflictException extends RuntimeException {

    public HotCloudResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotCloudResourceConflictException(String message) {
        super(message);
    }
}
