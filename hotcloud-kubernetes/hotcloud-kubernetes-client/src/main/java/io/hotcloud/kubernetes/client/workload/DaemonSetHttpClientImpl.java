package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
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
public class DaemonSetHttpClientImpl implements DaemonSetHttpClient {

    private final DaemonSetFeignClient daemonSetFeignClient;
    private final URI uri;

    public DaemonSetHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                   DaemonSetFeignClient daemonSetFeignClient) {
        this.daemonSetFeignClient = daemonSetFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<DaemonSet> read(String namespace, String daemonSet) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(daemonSet), "daemonSet name is null");
        return daemonSetFeignClient.read(uri, namespace, daemonSet).getBody();
    }

    @Override
    public Result<DaemonSetList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return daemonSetFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<DaemonSet> create(DaemonSetCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return daemonSetFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<DaemonSet> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return daemonSetFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String daemonSet) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(daemonSet), "daemonSet name is null");
        return daemonSetFeignClient.delete(uri, namespace, daemonSet).getBody();
    }
}
