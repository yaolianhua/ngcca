package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface CronJobClient {

    /**
     * Read namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link CronJob}
     */
    default CronJob read(String namespace, String cronJob) {
        return read(null, namespace, cronJob);
    }

    /**
     * Read namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link CronJob}
     */
    CronJob read(String agentUrl, String namespace, String cronJob);

    /**
     * Read namespaced CronJobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link CronJobList}
     */
    default CronJobList readList(String namespace, Map<String, String> labelSelector) {
        return readList(null, namespace, labelSelector);
    }

    /**
     * Read namespaced CronJobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link CronJobList}
     */
    CronJobList readList(String agentUrl, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced CronJobList
     *
     * @return {@link CronJobList}
     */
    default CronJobList readList() {
        return readList(null);
    }

    /**
     * Read all namespaced CronJobList
     *
     * @return {@link CronJobList}
     */
    CronJobList readList(String agentUrl);

    /**
     * Create CronJob from {@code CronJobCreateRequest}
     *
     * @param request {@link CronJobCreateRequest}
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default CronJob create(CronJobCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create CronJob from {@code CronJobCreateRequest}
     *
     * @param request {@link CronJobCreateRequest}
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    CronJob create(String agentUrl, CronJobCreateRequest request) throws ApiException;

    /**
     * Create CronJob from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default CronJob create(YamlBody yaml) throws ApiException {
        return create(null, yaml);
    }

    /**
     * Create CronJob from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    CronJob create(String agentUrl, YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String namespace, String cronJob) throws ApiException {
        return delete(null, namespace, cronJob);
    }

    /**
     * Delete namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agentUrl, String namespace, String cronJob) throws ApiException;

}
