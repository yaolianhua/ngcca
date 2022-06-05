package io.hotcloud.common.api.exception;

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
