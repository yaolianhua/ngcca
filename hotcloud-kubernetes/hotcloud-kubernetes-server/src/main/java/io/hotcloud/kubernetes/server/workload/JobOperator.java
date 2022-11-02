package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.api.UUIDGenerator;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.kubernetes.api.workload.JobApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobOperator implements JobApi {

    private final BatchV1Api batchV1Api;
    private final KubernetesClient fabric8Client;

    public JobOperator(BatchV1Api batchV1Api, KubernetesClient fabric8Client) {
        this.batchV1Api = batchV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Job create(String yaml) throws ApiException {
        V1Job v1Job;
        try {
            v1Job = Yaml.loadAs(yaml, V1Job.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load job yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Job.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : UUIDGenerator.DEFAULT;
        V1Job job = batchV1Api.createNamespacedJob(namespace,
                v1Job,
                "true",
                null,
                null, null);
        log.debug("create job success \n '{}'", job);

        return fabric8Client.batch()
                .v1()
                .jobs()
                .inNamespace(namespace)
                .withName(v1Job.getMetadata().getName())
                .get();
    }

    @Override
    public void delete(String namespace, String job) throws ApiException {
        Assert.hasText(namespace, () -> "namespace is null");
        Assert.hasText(job, () -> "delete resource name is null");
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

    @Override
    public JobList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.batch()
                    .v1()
                    .jobs()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        return fabric8Client.batch()
                .v1()
                .jobs()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();
    }
}
