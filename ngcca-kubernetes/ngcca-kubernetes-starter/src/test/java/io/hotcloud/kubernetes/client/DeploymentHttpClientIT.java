package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.DeploymentHttpClient;
import io.hotcloud.kubernetes.client.workload.PodHttpClient;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableHotCloudHttpClient
public class DeploymentHttpClientIT extends ClientIntegrationTestBase {

    private static final String DEPLOYMENT = "nginx";
    private static final String NAMESPACE = "default";
    @Autowired
    private DeploymentHttpClient deploymentHttpClient;
    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Deployment Client Integration Test Start");
        create();
        log.info("Create Deployment Name: '{}'", DEPLOYMENT);
    }

    @After
    public void post() throws ApiException {
        deploymentHttpClient.delete(NAMESPACE, DEPLOYMENT);
        log.info("Delete Deployment Name: '{}'", DEPLOYMENT);
        log.info("Deployment Client Integration Test End");
    }

    @Test
    public void scale() {

        deploymentHttpClient.scale(NAMESPACE, DEPLOYMENT, 3, true);

        Deployment read = deploymentHttpClient.read(NAMESPACE, DEPLOYMENT);
        Integer replicas = read.getSpec().getReplicas();

        Assert.assertEquals(3, (int) replicas);

    }

    @Test
    public void rolling_updateImage() {
        Deployment pause = deploymentHttpClient.rolling(RollingAction.PAUSE, NAMESPACE, DEPLOYMENT);

        Deployment imagesSet = deploymentHttpClient.imagesSet(NAMESPACE, DEPLOYMENT, Map.of("nginx", "nginx:1.21.6"));

        String image = imagesSet.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Assertions.assertEquals("nginx:1.21.6", image);

        Deployment imageSet = deploymentHttpClient.imageSet(NAMESPACE, DEPLOYMENT, "nginx:1.20.2");

        String image2 = imageSet.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Assertions.assertEquals("nginx:1.20.2", image2);

        Deployment resume = deploymentHttpClient.rolling(RollingAction.RESUME, NAMESPACE, DEPLOYMENT);

        Deployment undo = deploymentHttpClient.rolling(RollingAction.UNDO, NAMESPACE, DEPLOYMENT);
        String image3 = undo.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Assertions.assertEquals("nginx", image3);

        Deployment restart = deploymentHttpClient.rolling(RollingAction.RESTART, NAMESPACE, DEPLOYMENT);
    }

    @Test
    public void read() throws InterruptedException {
        DeploymentList readList = deploymentHttpClient.readList(NAMESPACE, Map.of("app", DEPLOYMENT));
        List<Deployment> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Deployment Name: {}", names);

        Deployment result = deploymentHttpClient.read(NAMESPACE, DEPLOYMENT);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, DEPLOYMENT);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        PodList podListResult = podHttpClient.readList(NAMESPACE, null);
        List<Pod> pods = podListResult.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .filter(e -> e.startsWith(DEPLOYMENT))
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);
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
        container.setImage("nginx");
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

        deploymentHttpClient.create(request);

    }

}
