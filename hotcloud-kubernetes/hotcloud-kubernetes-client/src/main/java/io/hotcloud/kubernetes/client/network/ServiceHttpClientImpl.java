package io.hotcloud.kubernetes.client.network;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
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
public class ServiceHttpClientImpl implements ServiceHttpClient {

    private final ServiceFeignClient serviceFeignClient;
    private final URI uri;

    public ServiceHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                 ServiceFeignClient serviceFeignClient) {
        this.serviceFeignClient = serviceFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<Service> read(String namespace, String service) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(service), "service name is null");
        return serviceFeignClient.read(uri, namespace, service).getBody();
    }

    @Override
    public Result<ServiceList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return serviceFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Service> create(ServiceCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return serviceFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Service> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return serviceFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String service) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(service), "service name is null");
        return serviceFeignClient.delete(uri, namespace, service).getBody();
    }
}
