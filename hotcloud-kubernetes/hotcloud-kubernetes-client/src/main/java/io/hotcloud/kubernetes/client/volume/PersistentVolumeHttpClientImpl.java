package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeCreateRequest;
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
public class PersistentVolumeHttpClientImpl implements PersistentVolumeHttpClient {

    private final PersistentVolumeFeignClient persistentVolumeFeignClient;
    private final URI uri;

    public PersistentVolumeHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                          PersistentVolumeFeignClient persistentVolumeFeignClient) {
        this.persistentVolumeFeignClient = persistentVolumeFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<PersistentVolume> read(String persistentVolume) {
        Assert.argument(StringUtils.hasText(persistentVolume), "persistentVolume name is null");
        return persistentVolumeFeignClient.read(uri, persistentVolume).getBody();
    }

    @Override
    public Result<PersistentVolumeList> readList(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return persistentVolumeFeignClient.readList(uri, labelSelector).getBody();
    }

    @Override
    public Result<PersistentVolume> create(PersistentVolumeCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return persistentVolumeFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<PersistentVolume> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return persistentVolumeFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String persistentVolume) throws ApiException {
        Assert.argument(StringUtils.hasText(persistentVolume), "persistentVolume name is null");
        return persistentVolumeFeignClient.delete(uri, persistentVolume).getBody();
    }
}
