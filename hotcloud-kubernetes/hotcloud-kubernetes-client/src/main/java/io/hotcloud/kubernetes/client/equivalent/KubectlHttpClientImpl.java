package io.hotcloud.kubernetes.client.equivalent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.equianlent.CopyAction;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class KubectlHttpClientImpl implements KubectlHttpClient {

    private static final String PATH = "/v1/kubernetes/equivalents";
    private final URI uri;
    private final RestTemplate restTemplate;

    public KubectlHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                 RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }


    @Override
    public Result<List<HasMetadata>> resourceListCreateOrReplace(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null", 400);
        Assert.hasText(yaml.getYaml(), "yaml content is null", 400);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<Result<List<HasMetadata>>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Boolean> delete(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null", 400);
        Assert.hasText(yaml.getYaml(), "yaml content is null", 400);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<Result<Boolean>> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Boolean> portForward(String namespace, String pod, String ipv4Address, Integer containerPort, Integer localPort, Long time, TimeUnit timeUnit) {

        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(pod, "pod name is null", 400);
        Assert.notNull(containerPort, "containerPort is null", 400);
        Assert.notNull(localPort, "localPort is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/forward", uri))
                .queryParam("ipv4Address", ipv4Address)
                .queryParam("containerPort", containerPort)
                .queryParam("localPort", localPort)
                .queryParam("alive", time)
                .queryParam("timeUnit", timeUnit)
                .build(namespace, pod);

        ResponseEntity<Result<Boolean>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<List<Event>> events(String namespace) {
        Assert.hasText(namespace, "namespace is null", 400);

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/events", uri))
                .build(namespace);

        ResponseEntity<Result<List<Event>>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Event> events(String namespace, String name) {
        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(name, "name is null", 400);

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/events/{name}", uri))
                .build(namespace, name);

        ResponseEntity<Result<Event>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Boolean> upload(String namespace, String pod, String container, String source, String target, CopyAction action) {
        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(pod, "pod name is null", 400);
        Assert.hasText(source, "source path  is null", 400);
        Assert.hasText(target, "target path is null", 400);
        Assert.notNull(action, "action is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/upload", uri))
                .queryParam("container", container)
                .queryParam("source", source)
                .queryParam("target", target)
                .queryParam("action", action)
                .build(namespace, pod);

        ResponseEntity<Result<Boolean>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Boolean> download(String namespace, String pod, String container, String source, String target, CopyAction action) {
        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(pod, "pod name is null", 400);
        Assert.hasText(source, "source path  is null", 400);
        Assert.hasText(target, "target path is null", 400);
        Assert.notNull(action, "action is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/download", uri))
                .queryParam("container", container)
                .queryParam("source", source)
                .queryParam("target", target)
                .queryParam("action", action)
                .build(namespace, pod);

        ResponseEntity<Result<Boolean>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
