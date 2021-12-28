package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
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
public class PersistentVolumeClaimHttpClientImpl implements PersistentVolumeClaimHttpClient {

    private final PersistentVolumeClaimFeignClient persistentVolumeClaimFeignClient;
    private final URI uri;

    public PersistentVolumeClaimHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                               PersistentVolumeClaimFeignClient persistentVolumeClaimFeignClient) {
        this.persistentVolumeClaimFeignClient = persistentVolumeClaimFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<PersistentVolumeClaim> read(String namespace, String persistentVolumeClaim) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(persistentVolumeClaim), "persistentVolumeClaim name is null");
        return persistentVolumeClaimFeignClient.read(uri, namespace, persistentVolumeClaim).getBody();
    }

    @Override
    public Result<PersistentVolumeClaimList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return persistentVolumeClaimFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<PersistentVolumeClaim> create(PersistentVolumeClaimCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return persistentVolumeClaimFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<PersistentVolumeClaim> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return persistentVolumeClaimFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String persistentVolumeClaim) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(persistentVolumeClaim), "persistentVolumeClaim name is null");
        return persistentVolumeClaimFeignClient.delete(uri, namespace, persistentVolumeClaim).getBody();
    }
}
