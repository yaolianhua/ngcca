package io.hotcloud.common.exception;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudResourceNotFoundException extends RuntimeException {

    public HotCloudResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotCloudResourceNotFoundException(String message) {
        super(message);
    }
}
