package io.hotcloud.server.registry;


import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.vendor.registry.RegistrySearchClient;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.dockerregistry.DockerRegistryCatalog;
import io.hotcloud.vendor.registry.model.dockerregistry.DockerRegistryTags;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.vendor.registry.model.RegistryUtil.retrieveRepositoryNameWithNamespace;

public class DockerRegistrySearchClient implements RegistrySearchClient {

    private final RestTemplate restTemplate;
    private final URI uri;

    public DockerRegistrySearchClient(RestTemplate restTemplate, URI uri) {
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    /**
     * <a href="https://docs.docker.com/registry/spec/api/#listing-repositories">接口文档</a>
     */
    @Override
    public PageResult<RegistryRepository> searchRepositories(RegistryAuthentication authentication, Pageable pageable, String query) {

        String requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/v2/_catalog")
                .toUriString();
        Log.info(DockerRegistrySearchClient.class.getName(), String.format("Docker registry repository search. request url '%s'", requestUrl));

        try {
            HttpEntity<?> httpEntity = HttpEntity.EMPTY;
            if (authentication.isBasicAuth()) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBasicAuth(authentication.getUsername(), authentication.getPassword());
                httpEntity = new HttpEntity<>(httpHeaders);
            }
            DockerRegistryCatalog dockerRegistryCatalog = restTemplate.exchange(requestUrl,
                    HttpMethod.GET,
                    httpEntity,
                    DockerRegistryCatalog.class).getBody();

            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepository> filteredRepositories = Objects.requireNonNull(dockerRegistryCatalog).getRepositories().stream()
                    .filter(name -> !StringUtils.hasText(query) || name.contains(query))
                    .map(e -> RegistryRepository.of(resolvedRegistry, e))
                    .collect(Collectors.toList());

            return PageResult.ofCollectionPage(filteredRepositories, pageable);
        } catch (Exception e) {
            throw new NGCCACommonException(e.getMessage(), 500);
        }
    }

    /**
     * <a href="https://docs.docker.com/registry/spec/api/#listing-image-tags">接口文档</a>
     *
     * @param repository The full path of the repository. e.g. namespace/name
     */
    @Override
    public PageResult<RegistryRepositoryTag> searchTags(RegistryAuthentication authentication, Pageable pageable, String repository) {
        String namespacedRepository = retrieveRepositoryNameWithNamespace(repository);
        URI requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/v2/{repository}/tags/list")
                .build(namespacedRepository);
        Log.info(DockerRegistrySearchClient.class.getName(), String.format("Docker registry repository tags search. request url '%s'", requestUrl));
        try {
            HttpEntity<?> httpEntity = HttpEntity.EMPTY;
            if (authentication.isBasicAuth()) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBasicAuth(authentication.getUsername(), authentication.getPassword());
                httpEntity = new HttpEntity<>(httpHeaders);
            }
            DockerRegistryTags registryTags = restTemplate.exchange(requestUrl,
                    HttpMethod.GET,
                    httpEntity,
                    DockerRegistryTags.class).getBody();

            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepositoryTag> tags = Objects.requireNonNull(registryTags).getTags().stream()
                    .map(e -> RegistryRepositoryTag.of(e, resolvedRegistry, namespacedRepository))
                    .collect(Collectors.toList());
            return PageResult.ofCollectionPage(tags, pageable);
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException ex) {
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new NGCCAResourceNotFoundException(String.format("repository '%s' not known to registry", repository));
                }
            }

            throw new NGCCACommonException(e.getMessage(), 500);
        }
    }
}
