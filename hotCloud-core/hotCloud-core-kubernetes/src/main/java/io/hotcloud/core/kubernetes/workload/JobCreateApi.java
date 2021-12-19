package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JobCreateApi {

    default Job job(JobCreateRequest request) throws ApiException {
        V1Job v1Job = JobBuilder.build(request);
        String json = Yaml.dump(v1Job);
        return this.job(json);
    }

    Job job(String yaml) throws ApiException;

}
