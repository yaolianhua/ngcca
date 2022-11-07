package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.ImagePullPolicy;
import io.hotcloud.kubernetes.model.pod.container.Port;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class PodHttpClientIT extends ClientIntegrationTestBase {

    private static final String POD = "nginx";
    private static final String NAMESPACE = "default";

    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Pod Client Integration Test Start");
        create();
        log.info("Create Pod Name: '{}'", POD);
    }

    @After
    public void post() throws ApiException {
        podHttpClient.delete(NAMESPACE, POD);
        log.info("Delete Pod Name: '{}'", POD);
        log.info("Pod Client Integration Test End");
    }

    @Test
    public void annotations_labels() {
        Pod annotatedPodResult = podHttpClient.addAnnotations(NAMESPACE, POD, Map.of("k8s-app", "nginx"));
        Map<String, String> annotations = annotatedPodResult.getMetadata().getAnnotations();
        Assertions.assertTrue(annotations.containsKey("k8s-app"));

        Pod labeledPodResult = podHttpClient.addLabels(NAMESPACE, POD, Map.of("k8s-app", "nginx"));
        Map<String, String> labels = labeledPodResult.getMetadata().getAnnotations();
        Assertions.assertTrue(labels.containsKey("k8s-app"));
    }

    @Test
    public void read() throws InterruptedException {
        PodList readList = podHttpClient.readList(NAMESPACE, null);
        List<Pod> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", names);

        Pod result = podHttpClient.read(NAMESPACE, POD);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, POD);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        List<String> podNames = items.stream()
                .map(e -> e.getMetadata().getName())
                .filter(e -> e.startsWith(POD))
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);

        for (String podName : podNames) {
            String logResult = podHttpClient.logs(NAMESPACE, podName, 100);
            log.info("Fetch Pod [{}] logs \n {}", podName, logResult);
        }
    }

    void create() throws ApiException {

        PodCreateRequest createRequest = new PodCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(POD);

        PodTemplateSpec templateSpec = new PodTemplateSpec();
        Container container = new Container();
        container.setName(POD);
        container.setImage("nginx:1.14.2");
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        Port port = new Port();
        port.setContainerPort(80);
        container.setPorts(List.of(port));

        templateSpec.setContainers(List.of(container));

        createRequest.setMetadata(objectMetadata);
        createRequest.setSpec(templateSpec);

        podHttpClient.create(createRequest);
    }

}
