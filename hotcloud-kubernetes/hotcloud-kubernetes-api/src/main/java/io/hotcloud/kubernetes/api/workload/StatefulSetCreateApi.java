package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface StatefulSetCreateApi {

    default StatefulSet statefulSet(StatefulSetCreateRequest request) throws ApiException {
        V1StatefulSet v1StatefulSet = StatefulSetBuilder.build(request);
        String json = Yaml.dump(v1StatefulSet);
        return this.statefulSet(json);
    }

    StatefulSet statefulSet(String yaml) throws ApiException;
}
