package io.hotcloud.kubernetes.client.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
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
public class ConfigMapHttpClientImpl implements ConfigMapHttpClient {

    private final ConfigMapFeignClient configMapFeignClient;
    private final URI uri;

    public ConfigMapHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                   ConfigMapFeignClient configMapFeignClient) {
        this.configMapFeignClient = configMapFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<ConfigMap> read(String namespace, String configmap) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(configmap), "configmap name is null");
        return configMapFeignClient.read(uri, namespace, configmap).getBody();
    }

    @Override
    public Result<ConfigMapList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return configMapFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<ConfigMap> create(ConfigMapCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return configMapFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<ConfigMap> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return configMapFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String configmap) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(configmap), "configmap name is null");
        return configMapFeignClient.delete(uri, namespace, configmap).getBody();
    }
}
