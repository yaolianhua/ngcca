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

    default CronJob cronjob(CronJobCreateRequest request) throws ApiException {
        V1CronJob v1CronJob = CronJobBuilder.build(request);
        String json = Yaml.dump(v1CronJob);
        return this.cronjob(json);
    }

    CronJob cronjob(String yaml) throws ApiException;

    void delete(String namespace, String cronjob) throws ApiException;

    default CronJob read(String namespace, String cronjob) {
        CronJobList cronJobList = this.read(namespace);
        return cronJobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), cronjob))
                .findFirst()
                .orElse(null);
    }

    default CronJobList read() {
        return this.read(null);
    }

    default CronJobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    CronJobList read(String namespace, Map<String, String> labelSelector);
}
