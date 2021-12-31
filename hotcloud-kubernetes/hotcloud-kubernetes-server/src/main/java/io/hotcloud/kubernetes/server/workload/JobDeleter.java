package io.hotcloud.kubernetes.server.workload;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.workload.JobDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobDeleter implements JobDeleteApi {

    private final BatchV1Api batchV1Api;

    public JobDeleter(BatchV1Api batchV1Api) {
        this.batchV1Api = batchV1Api;
    }

    @Override
    public void delete(String namespace, String job) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(job), () -> "delete resource name is null");
        V1Status v1Status = batchV1Api.deleteNamespacedJob(
                job,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        log.debug("delete namespaced job success \n '{}'", v1Status);
    }
}
