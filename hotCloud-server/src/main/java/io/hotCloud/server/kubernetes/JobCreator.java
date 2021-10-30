package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.HotCloudException;
import io.hotCloud.core.kubernetes.job.V1JobCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobCreator implements V1JobCreateApi {

    private final BatchV1Api batchV1Api;

    public JobCreator(BatchV1Api batchV1Api) {
        this.batchV1Api = batchV1Api;
    }

    @Override
    public V1Job job(String yaml) throws ApiException {
        V1Job v1Job;
        try {
            v1Job = (V1Job) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load job yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Job.getMetadata()).getNamespace();
        V1Job job = batchV1Api.createNamespacedJob(namespace,
                v1Job,
                "true",
                null,
                null);
        log.debug("create job success \n '{}'", job);
        return job;
    }
}
