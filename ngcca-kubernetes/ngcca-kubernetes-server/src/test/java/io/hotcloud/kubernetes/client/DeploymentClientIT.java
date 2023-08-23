package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.RollingAction;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.hotcloud.kubernetes.model.workload.DeploymentSpec;
import io.hotcloud.kubernetes.model.workload.DeploymentTemplate;
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
import java.util.stream.Collectors;

@Slf4j
@EnableKubernetesAgentClient
public class DeploymentClientIT extends ClientIntegrationTestBase {

    private static final String DEPLOYMENT = "jason-nginx";
    private static final String NAMESPACE = "default";
    @Autowired
    private DeploymentClient deploymentClient;

    @Before
    public void init() throws ApiException {
        log.info("Deployment Client Integration Test Start");
        create();
        log.info("Create Deployment: '{}'", DEPLOYMENT);
    }

    @After
    public void post() throws ApiException {
        deploymentClient.delete(NAMESPACE, DEPLOYMENT);
        log.info("Delete Deployment: '{}'", DEPLOYMENT);
        log.info("Deployment Client Integration Test End");
    }

    private void scale() {
        deploymentClient.scale(NAMESPACE, DEPLOYMENT, 3, true);

        Deployment read = deploymentClient.read(NAMESPACE, DEPLOYMENT);
        Integer replicas = read.getSpec().getReplicas();

        Assert.assertEquals(3, (int) replicas);

        deploymentClient.scale(NAMESPACE, DEPLOYMENT, 1, true);
    }

    private void rollingUpdate() throws InterruptedException {
        log.info("rolling deployment pause ...");
        deploymentClient.rolling(RollingAction.PAUSE, NAMESPACE, DEPLOYMENT);

        deploymentClient.rolling(RollingAction.RESUME, NAMESPACE, DEPLOYMENT);
        log.info("rolling deployment resume ...");

        Deployment imagesSet = deploymentClient.imagesSet(NAMESPACE, DEPLOYMENT, Map.of("nginx", "nginx:1.21.6"));
        log.info("set deployment container image to nginx:1.21.6");

        String image = imagesSet.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Assertions.assertEquals("nginx:1.21.6", image);
        waitPodRunningThenFetchContainerLogs(NAMESPACE, DEPLOYMENT, "nginx:1.21.6");


        Deployment undo = deploymentClient.rolling(RollingAction.UNDO, NAMESPACE, DEPLOYMENT);
        log.info("rolling deployment undo ...");

        String undoImage = undo.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Assertions.assertEquals("nginx:1.21.5", undoImage);
        waitPodRunningThenFetchContainerLogs(NAMESPACE, DEPLOYMENT, "nginx:1.21.5");

        deploymentClient.rolling(RollingAction.RESTART, NAMESPACE, DEPLOYMENT);
        log.info("rolling deployment restart ...");
    }

    @Test
    public void allinone() throws InterruptedException {
        //
        read();
        //
        scale();
        //
        rollingUpdate();

    }

    private void read() throws InterruptedException {
        DeploymentList readList = deploymentClient.readList(NAMESPACE, Map.of("app", DEPLOYMENT));
        List<Deployment> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Deployment: {}", names);

        Deployment result = deploymentClient.read(NAMESPACE, DEPLOYMENT);
        String name = result.getMetadata().getName();
        Assert.assertEquals(DEPLOYMENT, name);

        waitPodRunningThenFetchContainerLogs(NAMESPACE, DEPLOYMENT, "nginx:1.21.5");
    }

    void create() throws ApiException {

        Map<String, String> labels = Map.of("app", DEPLOYMENT);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(DEPLOYMENT);
        metadata.setLabels(labels);

        DeploymentSpec deploymentSpec = new DeploymentSpec();

        LabelSelector labelSelector = new LabelSelector();
        labelSelector.setMatchLabels(labels);
        deploymentSpec.setSelector(labelSelector);

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("nginx:1.21.5");
        container.setName("nginx");

        spec.setContainers(List.of(container));

        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(labels);

        DeploymentTemplate template = new DeploymentTemplate();
        template.setSpec(spec);
        template.setMetadata(templateMetadata);

        deploymentSpec.setTemplate(template);

        DeploymentCreateRequest request = new DeploymentCreateRequest();
        request.setSpec(deploymentSpec);
        request.setMetadata(metadata);

        deploymentClient.create(request);

    }

}
