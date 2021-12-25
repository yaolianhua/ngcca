package io.hotcloud.kubernetes.api.pod;

import io.fabric8.kubernetes.api.model.Pod;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PodCreateApi {

    default Pod pod(PodCreateRequest request) throws ApiException {
        V1Pod v1Pod = PodBuilder.build(request);
        String json = Yaml.dump(v1Pod);
        return this.pod(json);
    }

    Pod pod(String yaml) throws ApiException;
}
