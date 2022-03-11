package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface JobApi {

    default Job job(JobCreateRequest request) throws ApiException {
        V1Job v1Job = JobBuilder.build(request);
        String json = Yaml.dump(v1Job);
        return this.job(json);
    }

    Job job(String yaml) throws ApiException;

    void delete(String namespace, String job) throws ApiException;

    default Job read(String namespace, String job) {
        JobList jobList = this.read(namespace);
        return jobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), job))
                .findFirst()
                .orElse(null);
    }

    default JobList read() {
        return this.read(null);
    }

    default JobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    JobList read(String namespace, Map<String, String> labelSelector);
}
