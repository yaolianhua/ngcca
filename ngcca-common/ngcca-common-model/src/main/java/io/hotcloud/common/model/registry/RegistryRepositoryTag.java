package io.hotcloud.common.model.registry;

import io.hotcloud.common.model.registry.dockerhub.DockerHub;
import lombok.Data;

import static io.hotcloud.common.model.utils.RegistryUtil.retrieveRepositoryNameWithNamespace;

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
        String namespacedRepository = retrieveRepositoryNameWithNamespace(repository);
        RegistryRepositoryTag registryRepositoryTag = new RegistryRepositoryTag();
        registryRepositoryTag.setRegistry(registry);
        registryRepositoryTag.setRepository(repository);
        registryRepositoryTag.setImage(String.format("%s:%s", namespacedRepository, tag));
        registryRepositoryTag.setTag(tag);
        registryRepositoryTag.setImageName(String.format("%s/%s:%s", DockerHub.HTTP_URL.contains(registry) ? DockerHub.REGISTRY_HOST : registry, repository, tag));

        return registryRepositoryTag;
    }
}
