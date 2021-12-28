package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
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
public class StatefulSetHttpClientImpl implements StatefulSetHttpClient {

    private final StatefulSetFeignClient statefulSetFeignClient;
    private final URI uri;

    public StatefulSetHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                     StatefulSetFeignClient statefulSetFeignClient) {
        this.statefulSetFeignClient = statefulSetFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<StatefulSet> read(String namespace, String statefulSet) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(statefulSet), "statefulSet name is null");
        return statefulSetFeignClient.read(uri, namespace, statefulSet).getBody();
    }

    @Override
    public Result<StatefulSetList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return statefulSetFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<StatefulSet> create(StatefulSetCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return statefulSetFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<StatefulSet> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return statefulSetFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String statefulSet) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(statefulSet), "statefulSet name is null");
        return statefulSetFeignClient.delete(uri, namespace, statefulSet).getBody();
    }
}
