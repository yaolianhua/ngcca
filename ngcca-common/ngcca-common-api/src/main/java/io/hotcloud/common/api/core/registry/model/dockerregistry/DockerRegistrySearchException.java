package io.hotcloud.common.api.core.registry.model.dockerregistry;

public class DockerRegistrySearchException extends RuntimeException {

    private final int httpCode;

    public DockerRegistrySearchException() {
        super();
        this.httpCode = 500;
    }

    public DockerRegistrySearchException(int httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public DockerRegistrySearchException(String message) {
        super(message);
        this.httpCode = 500;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
