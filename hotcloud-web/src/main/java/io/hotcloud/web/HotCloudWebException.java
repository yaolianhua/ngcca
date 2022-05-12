package io.hotcloud.web;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudWebException extends Exception {

    private final int code;

    public HotCloudWebException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
