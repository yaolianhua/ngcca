package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.DeploymentHttpClient;
import io.hotcloud.kubernetes.client.workload.PodHttpClient;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
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
    public void read() throws InterruptedException {
        Result<DeploymentList> readList = deploymentHttpClient.readList(NAMESPACE, Map.of("app", DEPLOYMENT));
        List<Deployment> items = readList.getData().getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Deployment Name: {}", names);

        Result<Deployment> result = deploymentHttpClient.read(NAMESPACE, DEPLOYMENT);
        String name = result.getData().getMetadata().getName();
        Assert.assertEquals(name, DEPLOYMENT);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        Result<PodList> podListResult = podHttpClient.readList(NAMESPACE, null);
        List<Pod> pods = podListResult.getData().getItems();
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
