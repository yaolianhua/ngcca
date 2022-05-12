package io.hotcloud.web;

import lombok.Data;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CodeMessage {

    private final int code;
    private final String message;

    public CodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CodeMessage codeMessage(Throwable cause) {
        if (Objects.equals(HotCloudWebException.class, cause.getCause().getClass())) {
            HotCloudWebException exception = (HotCloudWebException) cause.getCause();
            return new CodeMessage(exception.getCode(), exception.getMessage());
        } else {
            return new CodeMessage(500, cause.getMessage());
        }
    }
}
