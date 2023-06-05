package io.hotcloud.vendor.registry.client;


import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.dockerhub.DockerHubRepositorySearchResult;
import io.hotcloud.vendor.registry.model.dockerhub.DockerHubTagSearchResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.vendor.registry.model.RegistryUtil.*;

class DockerHubRegistrySearchClient implements RegistrySearchClient {

    private final RestTemplate restTemplate;
    private final URI uri;

    public DockerHubRegistrySearchClient(RestTemplate restTemplate, URI uri) {
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    @Override
    public PageResult<RegistryRepository> searchRepositories(RegistryAuthentication authentication, Pageable pageable, String query) {

        String requestUrl = UriComponentsBuilder.fromUri(uri).path("/v2/search/repositories")
                .queryParam("page", pageable.getPage())
                .queryParam("page_size", pageable.getPageSize())
                .queryParam("query", query).toUriString();
        Log.debug(this, null, String.format("DockerHub repository search. request url '%s'", requestUrl));

        try {
            DockerHubRepositorySearchResult response = restTemplate.exchange(requestUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<DockerHubRepositorySearchResult>() {
                    }).getBody();

            List<RegistryRepository> repositories = Objects.requireNonNull(response).getResults().stream()
                    .map(e -> RegistryRepository.of(uri.getHost(), e.getRepository()))
                    .collect(Collectors.toList());

            return new PageResult<>(200, "success", repositories, response.getCount(), pageable.getPage(), pageable.getPageSize());
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }

    /**
     * <a href="https://docs.docker.com/docker-hub/api/latest/#tag/repositories/paths/~1v2~1namespaces~1%7Bnamespace%7D~1repositories~1%7Brepository%7D~1tags/get">接口文档</a>
     */
    @Override
    public PageResult<RegistryRepositoryTag> searchTags(RegistryAuthentication authentication, Pageable pageable, String repository) {

        String namespace = getNamespace(repository);
        String name = getImageName(repository);
        String namespacedImage = getNamespacedImageName(repository);

        URI requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/v2/namespaces/{namespace}/repositories/{repository}/tags")
                .queryParam("page", pageable.getPage())
                .queryParam("page_size", pageable.getPageSize())
                .build(namespace, name);
        Log.debug(this, null, String.format("DockerHub repository tags search. request url '%s'", requestUrl));
        try {
            DockerHubTagSearchResult response = restTemplate.exchange(requestUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<DockerHubTagSearchResult>() {
                    }).getBody();

            List<RegistryRepositoryTag> tags = Objects.requireNonNull(response).getResults().stream()
                    .map(e -> RegistryRepositoryTag.of(e.getName(), uri.getHost(), namespacedImage))
                    .collect(Collectors.toList());
            return new PageResult<>(200, "success", tags, response.getCount(), pageable.getPage(), pageable.getPageSize());
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }
}
