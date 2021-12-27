package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface CronJobHttpClient {

    /**
     * Read namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link Result}
     */
    Result<CronJob> read(String namespace, String cronJob);

    /**
     * Read namespaced CronJobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link Result}
     */
    Result<CronJobList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create CronJob from {@code CronJobCreateRequest}
     *
     * @param request {@link CronJobCreateRequest}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<CronJob> create(CronJobCreateRequest request) throws ApiException;

    /**
     * Create CronJob from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<CronJob> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced CronJob
     *
     * @param namespace namespace
     * @param cronJob   cronJob name
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String cronJob) throws ApiException;

}
