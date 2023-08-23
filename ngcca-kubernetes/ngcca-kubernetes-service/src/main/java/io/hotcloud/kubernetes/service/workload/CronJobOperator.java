package io.hotcloud.kubernetes.service.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.CronJobApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class CronJobOperator implements CronJobApi {

    private final BatchV1Api batchV1Api;
    private final KubernetesClient fabric8Client;

    public CronJobOperator(BatchV1Api batchV1Api, KubernetesClient fabric8Client) {
        this.batchV1Api = batchV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public CronJob create(String yaml) throws ApiException {
        V1CronJob v1CronJob;
        try {
            v1CronJob = Yaml.loadAs(yaml, V1CronJob.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("load cronjob yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1CronJob.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : "default";
        V1CronJob cronJob = batchV1Api.createNamespacedCronJob(namespace,
                v1CronJob,
                "true",
                null,
                null, null);
        Log.debug(this, yaml, String.format("create cronjob '%s' success", Objects.requireNonNull(cronJob.getMetadata()).getName()));

        return fabric8Client.batch()
                .v1()
                .cronjobs()
                .inNamespace(namespace)
                .withName(v1CronJob.getMetadata().getName())
                .get();
    }

    @Override
    public CronJobList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.batch()
                    .v1()
                    .cronjobs()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.batch()
                .v1()
                .cronjobs()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }

    @Override
    public void delete(String namespace, String cronjob) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(cronjob, () -> "delete resource name is null");
        batchV1Api.deleteNamespacedCronJob(
                cronjob,
                namespace,
                "true",
                null,
                null,
                null,
                "Foreground",
                null
        );
        Log.debug(this, null, String.format("delete '%s' namespaced cronjob '%s' success",namespace, cronjob));
    }
}
