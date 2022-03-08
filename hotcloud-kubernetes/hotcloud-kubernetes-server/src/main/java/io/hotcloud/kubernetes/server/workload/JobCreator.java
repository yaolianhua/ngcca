package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.api.workload.JobCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static io.hotcloud.kubernetes.model.NamespaceGenerator.DEFAULT_NAMESPACE;


/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobCreator implements JobCreateApi {

    private final BatchV1Api batchV1Api;
    private final KubernetesClient fabric8Client;

    public JobCreator(BatchV1Api batchV1Api, KubernetesClient fabric8Client) {
        this.batchV1Api = batchV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Job job(String yaml) throws ApiException {
        V1Job v1Job;
        try {
            v1Job = Yaml.loadAs(yaml, V1Job.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load job yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Job.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Job job = batchV1Api.createNamespacedJob(namespace,
                v1Job,
                "true",
                null,
                null);
        log.debug("create job success \n '{}'", job);

        Job j = fabric8Client.batch()
                .v1()
                .jobs()
                .inNamespace(namespace)
                .withName(v1Job.getMetadata().getName())
                .get();
        return j;
    }
}
