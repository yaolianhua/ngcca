package io.hotcloud.client.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.client.HotCloudHttpClientProperties;
import io.hotcloud.client.kubernetes.DeploymentFeignClient;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public class DeploymentHttpClient implements HotCloudDeploymentHttpClient {

    private final DeploymentFeignClient deploymentFeignClient;
    private final URI uri;

    public DeploymentHttpClient(HotCloudHttpClientProperties clientProperties,
                                DeploymentFeignClient deploymentFeignClient) {
        this.deploymentFeignClient = deploymentFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<Deployment> read(String namespace, String deployment) {
        return deploymentFeignClient.read(uri, namespace, deployment).getBody();
    }

    @Override
    public Result<DeploymentList> readList(String namespace, Map<String, String> labelSelector) {
        return deploymentFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Deployment> create(DeploymentCreateRequest request) throws ApiException {
        return deploymentFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Deployment> create(YamlBody yaml) throws ApiException {
        return deploymentFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String deployment) throws ApiException {
        return deploymentFeignClient.delete(uri, namespace, deployment).getBody();
    }
}
