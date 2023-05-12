package io.hotcloud.kubernetes.model;

import java.util.Objects;

public class RequestParamAssertion {

    public static void assertNamespaceNotNull(String namespace) {
        if (Objects.isNull(namespace) || namespace.isBlank()) {
            throw new IllegalArgumentException("Request namespace is null");
        }
    }

    public static void assertResourceNameNotNull(String resource) {
        if (Objects.isNull(resource) || resource.isBlank()) {
            throw new IllegalArgumentException("Request resource name is null");
        }
    }

    public static void assertBodyNotNull(Object body) {
        if (Objects.isNull(body)) {
            throw new IllegalArgumentException("Request body is null");
        }
    }
}
