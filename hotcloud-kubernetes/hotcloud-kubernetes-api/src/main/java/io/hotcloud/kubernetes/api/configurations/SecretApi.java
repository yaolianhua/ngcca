package io.hotcloud.kubernetes.api.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface SecretApi {

    default Secret secret(SecretCreateRequest request) throws ApiException {
        V1Secret v1Secret = SecretBuilder.build(request);
        String json = Yaml.dump(v1Secret);
        return this.secret(json);
    }

    Secret secret(String yaml) throws ApiException;

    void delete(String namespace, String secret) throws ApiException;

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
