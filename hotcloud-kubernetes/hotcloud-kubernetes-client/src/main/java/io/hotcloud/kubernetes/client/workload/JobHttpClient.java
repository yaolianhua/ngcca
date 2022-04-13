package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.kubernetes.model.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface JobHttpClient {

    /**
     * Read namespaced Job
     *
     * @param namespace namespace
     * @param job       job name
     * @return {@link Job}
     */
    Result<Job> read(String namespace, String job);

    /**
     * Read namespaced JobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link JobList}
     */
    Result<JobList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create Job from {@code JobCreateRequest}
     *
     * @param request {@link JobCreateRequest}
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Job> create(JobCreateRequest request) throws ApiException;

    /**
     * Create Job from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Job> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Job
     *
     * @param namespace namespace
     * @param job       job name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String job) throws ApiException;

}
