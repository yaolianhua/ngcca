package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class DeploymentHttpClient implements HotCloudDeploymentHttpClient {

    private final DeploymentFeignClient deploymentFeignClient;
    private final URI uri;

    public DeploymentHttpClient(HotCloudHttpClientProperties clientProperties,
                                DeploymentFeignClient deploymentFeignClient) {
        this.deploymentFeignClient = deploymentFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<Deployment> read(String namespace, String deployment) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), "deployment name is null");
        return deploymentFeignClient.read(uri, namespace, deployment).getBody();
    }

    @Override
    public Result<DeploymentList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return deploymentFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Deployment> create(DeploymentCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return deploymentFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Deployment> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return deploymentFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String deployment) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), "deployment name is null");
        return deploymentFeignClient.delete(uri, namespace, deployment).getBody();
    }
}
