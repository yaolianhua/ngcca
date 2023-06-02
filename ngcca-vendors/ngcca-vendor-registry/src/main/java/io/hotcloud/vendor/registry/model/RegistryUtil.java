package io.hotcloud.vendor.registry.model;

import io.hotcloud.vendor.registry.model.dockerhub.DockerHub;
import org.springframework.util.Assert;

public final class RegistryUtil {
    private RegistryUtil() {
    }

    /**
     * 从repository名称获取仓库命名空间
     *
     * @param repository 不能包含仓库地址和镜像标签 e.g. library/nginx
     * @return library
     */
    public static String getNamespace(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return DockerHub.OFFICIAL_IMAGE_PREFIX;
        }

        return repository.substring(0, repository.indexOf("/"));
    }

    /**
     * 从repository名称获取不带有命名空间的镜像名称
     *
     * @param repository 不能包含仓库地址和镜像标签 e.g. library/nginx
     * @return nginx
     */
    public static String getImageNameOnly(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return repository;
        }

        return repository.substring(repository.indexOf("/") + 1);
    }

    /**
     * 从repository名称获取带有命名空间的镜像名称
     *
     * @param repository 不能包含仓库地址和镜像标签 e.g. library/nginx，nginx
     * @return library/nginx
     */
    public static String getNamespacedImage(String repository) {
        Assert.hasText(repository, "repository is null");
        if (!repository.contains("/")) {
            return String.format("%s/%s", DockerHub.OFFICIAL_IMAGE_PREFIX, repository);
        }

        return repository;
    }
}
