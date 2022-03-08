package io.hotcloud.common;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudException extends RuntimeException {

    private int code;

    public HotCloudException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotCloudException(String message) {
        this(message, 403);
    }

    public HotCloudException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
