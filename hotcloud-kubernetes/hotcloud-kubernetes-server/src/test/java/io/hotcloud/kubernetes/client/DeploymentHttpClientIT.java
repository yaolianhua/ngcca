package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.IntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.DeploymentHttpClient;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class DeploymentHttpClientIT extends IntegrationTestBase {

    private static final String DEPLOYMENT = "nginx";
    private static final String NAMESPACE = "default";
    @Autowired
    private DeploymentHttpClient deploymentHttpClient;

    @Before
    public void init() throws ApiException {
        create();
        log.info("Deployment Client Integration Test Start");
    }

    @After
    public void post() throws ApiException {
        deploymentHttpClient.delete(NAMESPACE, DEPLOYMENT);
        log.info("Deployment Client Integration Test End");
    }

    @Test
    public void read() {
        Result<DeploymentList> readList = deploymentHttpClient.readList(NAMESPACE, null);
        List<Deployment> deployments = readList.getData().getItems();
        Assert.assertTrue(deployments.size() > 0);

        Result<Deployment> result = deploymentHttpClient.read(NAMESPACE, DEPLOYMENT);
        String deployment = result.getData().getMetadata().getName();
        Assert.assertEquals(deployment, DEPLOYMENT);
    }

    void create() throws ApiException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(DEPLOYMENT);

        DeploymentSpec deploymentSpec = new DeploymentSpec();

        LabelSelector labelSelector = new LabelSelector();
        labelSelector.setMatchLabels(Map.of("app", DEPLOYMENT));
        deploymentSpec.setSelector(labelSelector);

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("nginx");
        container.setName("nginx");

        spec.setContainers(List.of(container));

        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(Map.of("app", DEPLOYMENT));

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
