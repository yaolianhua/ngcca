package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface JobClient {

    /**
     * Read namespaced Job
     *
     * @param namespace namespace
     * @param job       job name
     * @return {@link Job}
     */
    Job read(String namespace, String job);

    /**
     * Read namespaced JobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link JobList}
     */
    JobList readList(String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced JobList
     *
     * @return {@link JobList}
     */
    JobList readList();

    /**
     * Create Job from {@code JobCreateRequest}
     *
     * @param request {@link JobCreateRequest}
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Job create(JobCreateRequest request) throws ApiException;

    /**
     * Create Job from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Job}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Job create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Job
     *
     * @param namespace namespace
     * @param job    job name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String namespace, String job) throws ApiException;

}
