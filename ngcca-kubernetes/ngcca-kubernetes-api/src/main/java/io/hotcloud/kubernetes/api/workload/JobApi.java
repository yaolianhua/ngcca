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
    /**
     * Create Job from {@code JobCreateRequest}
     *
     * @param request {@link JobCreateRequest}
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Job create(JobCreateRequest request) throws ApiException {
        V1Job v1Job = JobBuilder.build(request);
        String json = Yaml.dump(v1Job);
        return this.create(json);
    }

    /**
     * Create Job from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Job create(String yaml) throws ApiException;

    /**
     * Delete namespaced Job
     *
     * @param namespace namespace
     * @param job       job name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String job) throws ApiException;

    /**
     * Read namespaced Job
     *
     * @param namespace namespace
     * @param job       job name
     * @return {@link Job}
     */
    default Job read(String namespace, String job) {
        JobList jobList = this.read(namespace);
        return jobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), job))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read JobList all namespace
     *
     * @return {@link JobList}
     */
    default JobList read() {
        return this.read(null);
    }

    /**
     * Read namespaced JobList
     *
     * @param namespace namespace
     * @return {@link JobList}
     */
    default JobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced JobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link JobList}
     */
    JobList read(String namespace, Map<String, String> labelSelector);
}
