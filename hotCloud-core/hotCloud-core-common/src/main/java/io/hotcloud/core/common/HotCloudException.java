package io.hotcloud.core.common;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudException extends RuntimeException{

    public HotCloudException(String message) {
        super(message);
    }

    public HotCloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
