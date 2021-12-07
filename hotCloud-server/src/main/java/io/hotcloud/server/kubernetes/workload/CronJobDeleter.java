package io.hotcloud.server.kubernetes.workload;

import io.hotcloud.core.kubernetes.workload.CronJobDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class CronJobDeleter implements CronJobDeleteApi {

    private final BatchV1Api batchV1Api;

    public CronJobDeleter(BatchV1Api batchV1Api) {
        this.batchV1Api = batchV1Api;
    }

    @Override
    public void delete(String namespace, String cronjob) throws ApiException {

        V1Status v1Status = batchV1Api.deleteNamespacedCronJob(
                cronjob,
                namespace,
                "true",
                null,
                null,
                null,
                null,
                null
        );
        log.debug("delete namespaced cronjob success \n '{}'", v1Status);
    }
}
