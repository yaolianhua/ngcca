package io.hotcloud.common.model.registry;

import io.hotcloud.common.model.registry.dockerhub.DockerHub;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class RegistryRepository {

    /**
     * repository name e.g. library/nginx
     */
    private String name;
    /**
     * registry url e.g. harbor.local:5000
     */
    private String registry;

    public static RegistryRepository of(String registry, String name) {
        final RegistryRepository repository = new RegistryRepository();
        repository.setRegistry(registry);
        if (StringUtils.hasText(name) && !name.contains("/")) {
            name = String.format("%s/%s", DockerHub.OFFICIAL_IMAGE_PREFIX, name);
        }
        repository.setName(name);
        return repository;
    }
}
