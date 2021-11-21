package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.workload.JobReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobReader implements JobReadApi {

    private final KubernetesClient fabric8Client;

    public JobReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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

        JobList jobList = fabric8Client.batch()
                .v1()
                .jobs()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return jobList;
    }
}
