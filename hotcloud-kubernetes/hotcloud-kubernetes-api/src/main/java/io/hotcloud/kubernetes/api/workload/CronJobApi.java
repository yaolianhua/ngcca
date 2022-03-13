package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface CronJobApi {
    /**
     * Create CronJob from {@code CronJobCreateRequest}
     *
     * @param request {@link CronJobCreateRequest}
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default CronJob cronjob(CronJobCreateRequest request) throws ApiException {
        V1CronJob v1CronJob = CronJobBuilder.build(request);
        String json = Yaml.dump(v1CronJob);
        return this.cronjob(json);
    }

    /**
     * Create CronJob from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link CronJob}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    CronJob cronjob(String yaml) throws ApiException;

    /**
     * Delete namespaced CronJob
     *
     * @param namespace namespace
     * @param cronjob   cronJob name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String cronjob) throws ApiException;

    /**
     * Read namespaced CronJob
     *
     * @param namespace namespace
     * @param cronjob   cronJob name
     * @return {@link CronJob}
     */
    default CronJob read(String namespace, String cronjob) {
        CronJobList cronJobList = this.read(namespace);
        return cronJobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), cronjob))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read CronJobList all namespace
     *
     * @return {@link CronJobList}
     */
    default CronJobList read() {
        return this.read(null);
    }

    /**
     * Read namespaced CronJobList
     *
     * @param namespace namespace
     * @return {@link CronJobList}
     */
    default CronJobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced CronJobList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link CronJobList}
     */
    CronJobList read(String namespace, Map<String, String> labelSelector);
}
