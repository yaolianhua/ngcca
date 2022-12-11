package io.hotcloud.common.api.core.registry.model.quay;

public class QuaySearchException extends RuntimeException {

    private final int httpCode;

    public QuaySearchException() {
        super();
        this.httpCode = 500;
    }

    public QuaySearchException(String message) {
        super(message);
        this.httpCode = 500;
    }

    public QuaySearchException(int httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
