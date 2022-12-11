package io.hotcloud.common.api.core.registry.model;

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
            name = String.format("library/%s", name);
        }
        repository.setName(name);
        return repository;
    }
}
