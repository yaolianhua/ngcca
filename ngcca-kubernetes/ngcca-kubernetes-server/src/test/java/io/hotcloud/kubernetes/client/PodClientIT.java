package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.ImagePullPolicy;
import io.hotcloud.kubernetes.model.pod.container.Port;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@EnableKubernetesAgentClient
public class PodClientIT extends ClientIntegrationTestBase {

    private static final String POD = "nginx";
    private static final String NAMESPACE = "default";

    @Autowired
    private PodClient podClient;

    @Before
    public void init() throws ApiException {
        create();
        waitPodRunningThenFetchContainerLogs(NAMESPACE, POD, "nginx:1.21.5");
    }

    @After
    public void post() throws ApiException {
        podClient.delete(NAMESPACE, POD);
        printNamespacedEvents(NAMESPACE, POD);
    }

    @Test
    public void annotations_labels() {
        Pod annotatedPodResult = podClient.addAnnotations(NAMESPACE, POD, Map.of("k8s-app", "nginx"));
        Map<String, String> annotations = annotatedPodResult.getMetadata().getAnnotations();
        Assertions.assertTrue(annotations.containsKey("k8s-app"));

        Pod labeledPodResult = podClient.addLabels(NAMESPACE, POD, Map.of("k8s-app", "nginx"));
        Map<String, String> labels = labeledPodResult.getMetadata().getAnnotations();
        Assertions.assertTrue(labels.containsKey("k8s-app"));
    }

    void create() throws ApiException {

        PodCreateRequest createRequest = new PodCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(POD);

        PodTemplateSpec templateSpec = new PodTemplateSpec();
        Container container = new Container();
        container.setName(POD);
        container.setImage("nginx:1.21.5");
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        Port port = new Port();
        port.setContainerPort(80);
        container.setPorts(List.of(port));

        templateSpec.setContainers(List.of(container));

        createRequest.setMetadata(objectMetadata);
        createRequest.setSpec(templateSpec);

        podClient.create(createRequest);
    }

}
