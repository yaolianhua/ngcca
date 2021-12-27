package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
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
public class CronJobHttpClientImpl implements CronJobHttpClient {

    private final CronJobFeignClient cronJobFeignClient;
    private final URI uri;

    public CronJobHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                 CronJobFeignClient cronJobFeignClient) {
        this.cronJobFeignClient = cronJobFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<CronJob> read(String namespace, String cronJob) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(cronJob), "cronJob name is null");
        return cronJobFeignClient.read(uri, namespace, cronJob).getBody();
    }

    @Override
    public Result<CronJobList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return cronJobFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<CronJob> create(CronJobCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return cronJobFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<CronJob> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return cronJobFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String cronJob) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(cronJob), "cronJob name is null");
        return cronJobFeignClient.delete(uri, namespace, cronJob).getBody();
    }
}
