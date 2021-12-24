package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.workload.CronJobReadApi;
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
public class CronJobReader implements CronJobReadApi {

    private final KubernetesClient fabric8Client;

    public CronJobReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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

        CronJobList cronJobList = fabric8Client.batch()
                .v1()
                .cronjobs()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return cronJobList;
    }
}
