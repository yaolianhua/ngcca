package io.hotCloud.core.kubernetes.job;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface V1JobCreateApi {

    default V1Job job(JobCreateParams request) throws ApiException {
        V1Job v1Job = V1JobBuilder.buildV1Job(request);
        String json = Yaml.dump(v1Job);
        return this.job(json);
    }

    V1Job job(String yaml) throws ApiException;

}
