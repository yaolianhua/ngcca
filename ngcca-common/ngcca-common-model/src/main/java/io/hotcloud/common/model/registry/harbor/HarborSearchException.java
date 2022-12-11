package io.hotcloud.common.model.registry.harbor;

public class HarborSearchException extends RuntimeException {

    private final int httpCode;

    public HarborSearchException() {
        super();
        this.httpCode = 500;
    }

    public HarborSearchException(int httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public HarborSearchException(String message) {
        super(message);
        this.httpCode = 500;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
