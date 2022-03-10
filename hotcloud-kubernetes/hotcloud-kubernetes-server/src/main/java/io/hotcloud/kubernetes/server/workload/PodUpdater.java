package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.pod.PodUpdateApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodUpdater implements PodUpdateApi {

    private final KubernetesClient fabric8Client;

    public PodUpdater(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Pod addAnnotations(String namespace, String pod, Map<String, String> annotations) {
        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(pod, "pod name is null", 400);
        Assert.argument(!CollectionUtils.isEmpty(annotations), "annotations is empty");

        Pod newPod = fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .edit(
                        p -> new PodBuilder(p)
                                .editMetadata()
                                .addToAnnotations(annotations)
                                .endMetadata()
                                .build()
                );
        log.debug("Annotated annotations '{}' for pod '{}' in '{}'", annotations, pod, namespace);
        return newPod;
    }

    @Override
    public Pod addLabels(String namespace, String pod, Map<String, String> labels) {
        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(pod, "pod name is null", 400);
        Assert.argument(!CollectionUtils.isEmpty(labels), "labels is empty");

        Pod newPod = fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .edit(
                        p -> new PodBuilder(p)
                                .editMetadata()
                                .addToLabels(labels)
                                .endMetadata()
                                .build()
                );
        log.debug("Tag labels '{}' for pod '{}' in '{}'", labels, pod, namespace);
        return newPod;
    }
}
