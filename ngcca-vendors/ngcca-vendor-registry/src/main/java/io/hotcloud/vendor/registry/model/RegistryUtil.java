package io.hotcloud.vendor.registry.model;

import io.hotcloud.vendor.registry.model.dockerhub.DockerHub;
import org.springframework.util.Assert;

public final class RegistryUtil {
    private RegistryUtil() {
    }

    /**
     * 检索仓库namespace
     *
     * @param repository 镜像仓库名称 e.g. library/nginx
     * @return namespace
     */
    public static String retrieveRepositoryNamespace(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return DockerHub.OFFICIAL_IMAGE_PREFIX;
        }

        return repository.substring(0, repository.indexOf("/"));
    }

    /**
     * 检索不带有namespace的仓库镜像名
     *
     * @param repository e.g. library/nginx
     * @return repository name
     */
    public static String retrieveRepositoryNameWithNoNamespace(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return repository;
        }

        return repository.substring(repository.indexOf("/") + 1);
    }

    /**
     * 检索带有namespace的仓库镜像名
     *
     * @param repository e.g. library/nginx nginx
     * @return repository name
     */
    public static String retrieveRepositoryNameWithNamespace(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return String.format("%s/%s", DockerHub.OFFICIAL_IMAGE_PREFIX, repository);
        }

        return repository;
    }
}
