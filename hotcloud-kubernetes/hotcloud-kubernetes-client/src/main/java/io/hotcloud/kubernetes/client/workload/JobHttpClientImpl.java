package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
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
public class JobHttpClientImpl implements JobHttpClient {

    private final JobFeignClient jobFeignClient;
    private final URI uri;

    public JobHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                             JobFeignClient jobFeignClient) {
        this.jobFeignClient = jobFeignClient;
        uri = URI.create(clientProperties.obtainUrl());
    }

    @Override
    public Result<Job> read(String namespace, String job) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(job), "job name is null");
        return jobFeignClient.read(uri, namespace, job).getBody();
    }

    @Override
    public Result<JobList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        return jobFeignClient.readList(uri, namespace, labelSelector).getBody();
    }

    @Override
    public Result<Job> create(JobCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);
        return jobFeignClient.create(uri, request).getBody();
    }

    @Override
    public Result<Job> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        return jobFeignClient.create(uri, yaml).getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String job) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(job), "job name is null");
        return jobFeignClient.delete(uri, namespace, job).getBody();
    }
}
