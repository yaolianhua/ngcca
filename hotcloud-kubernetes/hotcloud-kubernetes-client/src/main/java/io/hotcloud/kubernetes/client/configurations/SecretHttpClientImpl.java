package io.hotcloud.kubernetes.client.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
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
public class SecretHttpClientImpl implements SecretHttpClient {

    private final SecretFeignClient secretFeignClient;
    private final URI uri;

    public SecretHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                SecretFeignClient secretFeignClient) {
        this.secretFeignClient = secretFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<Secret> read(String namespace, String secret) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(secret), "secret name is null");
        return secretFeignClient.read(uri, namespace, secret).getBody();
    }

    @Override
    public Result<SecretList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return secretFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Secret> create(SecretCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return secretFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Secret> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return secretFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String secret) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(secret), "secret name is null");
        return secretFeignClient.delete(uri, namespace, secret).getBody();
    }
}
