package io.hotcloud.kubernetes.client.factory;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.kubernetes.client.NgccaKubernetesAgentProperties;
import io.hotcloud.kubernetes.client.equivalent.KubectlHttpClient;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
class KubectlHttpClientImpl implements KubectlHttpClient {

    private static final String PATH = "/v1/kubernetes/equivalents";
    private final URI uri;
    private final RestTemplate restTemplate;

    public KubectlHttpClientImpl(NgccaKubernetesAgentProperties clientProperties,
                                 RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }


    @Override
    public List<HasMetadata> resourceListCreateOrReplace(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null");
        Assert.hasText(yaml.getYaml(), "yaml content is null");

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<List<HasMetadata>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Boolean delete(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null");
        Assert.hasText(yaml.getYaml(), "yaml content is null");

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<Boolean> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Boolean portForward(String namespace, String pod, String ipv4Address, Integer containerPort, Integer localPort, Long time, TimeUnit timeUnit) {

        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.notNull(containerPort, "containerPort is null");
        Assert.notNull(localPort, "localPort is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/forward", uri))
                .queryParam("ipv4Address", ipv4Address)
                .queryParam("containerPort", containerPort)
                .queryParam("localPort", localPort)
                .queryParam("alive", time)
                .queryParam("timeUnit", timeUnit)
                .build(namespace, pod);

        ResponseEntity<Boolean> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<Event> events(String namespace) {
        Assert.hasText(namespace, "namespace is null");

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/events", uri))
                .build(namespace);

        ResponseEntity<List<Event>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<Event> events() {

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/events", uri)).build().toUri();

        ResponseEntity<List<Event>> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<Event> namespacedPodEvents(String namespace, String pod) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/{pod}/events", uri))
                .build(namespace, pod);

        ResponseEntity<List<Event>> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Event events(String namespace, String name) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(name, "name is null");

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/events/{name}", uri))
                .build(namespace, name);

        ResponseEntity<Event> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Boolean upload(String namespace, String pod, String container, String source, String target, CopyAction action) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");
        Assert.notNull(action, "action is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/upload", uri))
                .queryParam("container", container)
                .queryParam("source", source)
                .queryParam("target", target)
                .queryParam("action", action)
                .build(namespace, pod);

        ResponseEntity<Boolean> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Boolean download(String namespace, String pod, String container, String source, String target, CopyAction action) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");
        Assert.notNull(action, "action is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/download", uri))
                .queryParam("container", container)
                .queryParam("source", source)
                .queryParam("target", target)
                .queryParam("action", action)
                .build(namespace, pod);

        ResponseEntity<Boolean> response = restTemplate.exchange(uriRequest, HttpMethod.POST, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
