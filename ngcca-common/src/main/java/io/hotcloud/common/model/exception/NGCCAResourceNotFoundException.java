package io.hotcloud.common.model.exception;

/**
 * @author yaolianhua789@gmail.com
 **/
public class NGCCAResourceNotFoundException extends RuntimeException {

    public NGCCAResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NGCCAResourceNotFoundException(String message) {
        super(message);
    }
}
