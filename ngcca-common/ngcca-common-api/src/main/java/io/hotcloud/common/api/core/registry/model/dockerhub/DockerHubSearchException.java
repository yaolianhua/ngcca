package io.hotcloud.common.api.core.registry.model.dockerhub;

public class DockerHubSearchException extends RuntimeException {

    private final int httpCode;

    public DockerHubSearchException() {
        super();
        this.httpCode = 500;
    }

    public DockerHubSearchException(int httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public DockerHubSearchException(String message) {
        super(message);
        this.httpCode = 500;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
