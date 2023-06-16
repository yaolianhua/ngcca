package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
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
class KubectlClientImpl implements KubectlClient {
    private final URI uri;
    private final RestTemplate restTemplate;

    public KubectlClientImpl(KubernetesAgentProperties clientProperties,
                             RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getAgentHttpUrl() + "/v1/kubernetes/equivalents");
    }


    @Override
    public List<HasMetadata> resourceListCreateOrReplace(String namespace, YamlBody yaml) {

        RequestParamAssertion.assertBodyNotNull(yaml);
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
        RequestParamAssertion.assertBodyNotNull(yaml);
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

        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);

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
        RequestParamAssertion.assertNamespaceNotNull(namespace);

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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);

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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(name);

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/{namespace}/events/{name}", uri))
                .build(namespace, name);

        ResponseEntity<Event> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Boolean upload(String namespace, String pod, String container, String source, String target, CopyAction action) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
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
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
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

    @Override
    public List<NodeMetrics> topNode() {

        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/nodemetrics", uri)).build().toUri();

        ResponseEntity<List<NodeMetrics>> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<PodMetrics> topPod() {
        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/podmetrics", uri)).build().toUri();

        ResponseEntity<List<PodMetrics>> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<Node> listNode() {
        URI uriRequest = UriComponentsBuilder.fromHttpUrl(String.format("%s/nodes", uri)).build().toUri();

        ResponseEntity<List<Node>> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
