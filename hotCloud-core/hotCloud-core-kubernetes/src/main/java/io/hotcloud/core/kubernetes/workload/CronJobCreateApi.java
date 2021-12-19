package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface CronJobCreateApi {

    default CronJob cronjob(CronJobCreateRequest request) throws ApiException {
        V1CronJob v1CronJob = CronJobBuilder.build(request);
        String json = Yaml.dump(v1CronJob);
        return this.cronjob(json);
    }

    CronJob cronjob(String yaml) throws ApiException;
}
