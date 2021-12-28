package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class PodHttpClientImpl implements PodHttpClient {

    private final PodFeignClient podFeignClient;
    private final URI uri;

    public PodHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                             PodFeignClient podFeignClient) {
        this.podFeignClient = podFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<String> logs(String namespace, String pod, Integer tail) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(pod), "pod name is null");
        return podFeignClient.logs(uri, namespace, pod, tail).getBody();
    }

    @Override
    public Result<List<String>> loglines(String namespace, String pod, Integer tail) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(pod), "pod name is null");
        return podFeignClient.loglines(uri, namespace, pod, tail).getBody();
    }

    @Override
    public Result<Pod> read(String namespace, String pod) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(pod), "pod name is null");
        return podFeignClient.read(uri, namespace, pod).getBody();
    }

    @Override
    public Result<PodList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return podFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Pod> create(PodCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return podFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Pod> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return podFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String pod) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(pod), "pod name is null");
        return podFeignClient.delete(uri, namespace, pod).getBody();
    }
}
