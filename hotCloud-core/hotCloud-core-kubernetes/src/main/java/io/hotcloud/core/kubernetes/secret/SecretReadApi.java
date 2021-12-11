package io.hotcloud.core.kubernetes.secret;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface SecretReadApi {

    default Secret read(String namespace, String secret) {
        SecretList secretList = this.read(namespace);
        return secretList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), secret))
                .findFirst()
                .orElse(null);
    }

    default SecretList read() {
        return this.read(null);
    }

    default SecretList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    SecretList read(String namespace, Map<String, String> labelSelector);
}
