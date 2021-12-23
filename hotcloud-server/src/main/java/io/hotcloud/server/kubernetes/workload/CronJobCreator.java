package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.workload.CronJobCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static io.hotcloud.core.kubernetes.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class CronJobCreator implements CronJobCreateApi {

    private final BatchV1Api batchV1Api;
    private final KubernetesClient fabric8Client;

    public CronJobCreator(BatchV1Api batchV1Api, KubernetesClient fabric8Client) {
        this.batchV1Api = batchV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public CronJob cronjob(String yaml) throws ApiException {
        V1CronJob v1CronJob;
        try {
            v1CronJob = Yaml.loadAs(yaml, V1CronJob.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load cronjob yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1CronJob.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1CronJob cronJob = batchV1Api.createNamespacedCronJob(namespace,
                v1CronJob,
                "true",
                null,
                null);
        log.debug("create cronjob success \n '{}'", cronJob);

        CronJob j = fabric8Client.batch()
                .v1()
                .cronjobs()
                .inNamespace(namespace)
                .withName(v1CronJob.getMetadata().getName())
                .get();
        return j;
    }
}
