package io.hotcloud.vendor.registry.model;

import io.hotcloud.vendor.registry.model.dockerhub.DockerHub;
import lombok.Data;

import static io.hotcloud.vendor.registry.model.RegistryUtil.getNamespacedImage;

@Data
public class RegistryRepositoryTag {

    /**
     * image tag e.g. {@code latest}
     */
    private String tag;
    /**
     * image name e.g. {@code nginx:latest}
     */
    private String image;
    /**
     * image full name e.g. {@code harbor.local:5000/library/nginx:latest}
     */
    private String imageName;
    /**
     * registry url e.g. {@code harbor.local:5000}
     */
    private String registry;
    /**
     * repository name e.g. {@code library/nginx}
     */
    private String repository;

    public static RegistryRepositoryTag of(String tag, String registry, String repository) {
        String namespacedImage = getNamespacedImage(repository);
        RegistryRepositoryTag registryRepositoryTag = new RegistryRepositoryTag();
        registryRepositoryTag.setRegistry(registry);
        registryRepositoryTag.setRepository(repository);
        registryRepositoryTag.setImage(String.format("%s:%s", namespacedImage, tag));
        registryRepositoryTag.setTag(tag);
        registryRepositoryTag.setImageName(String.format("%s/%s:%s", DockerHub.HTTP_URL.contains(registry) ? DockerHub.REGISTRY_HOST : registry, repository, tag));

        return registryRepositoryTag;
    }
}
