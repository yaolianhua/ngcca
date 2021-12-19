package io.hotcloud.core.kubernetes.secret;

import io.fabric8.kubernetes.api.model.Secret;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface SecretCreateApi {

    default Secret secret(SecretCreateRequest request) throws ApiException {
        V1Secret v1Secret = SecretBuilder.build(request);
        String json = Yaml.dump(v1Secret);
        return this.secret(json);
    }

    Secret secret(String yaml) throws ApiException;
}
