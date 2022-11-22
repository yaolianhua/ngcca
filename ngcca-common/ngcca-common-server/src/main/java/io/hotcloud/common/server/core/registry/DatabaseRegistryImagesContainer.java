package io.hotcloud.common.server.core.registry;

import io.hotcloud.common.api.core.registry.DatabaseRegistryImages;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DatabaseRegistryImagesContainer implements DatabaseRegistryImages {

    private final Map<String, String> images = new ConcurrentHashMap<>(256);

    /**
     * Put image to containers
     *
     * @param key   image name e.g. minio
     * @param value image url e.g. harbor.local:5000/library/minio:latest
     */
    public void put(String key, String value) {
        images.put(key, value);
    }

    /**
     * get image from containers
     *
     * @param key image name e.g. minio
     * @return image url e.g. harbor.local:5000/library/minio:latest
     */
    public String get(String key) {
        String value = images.get(key);
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new NGCCAResourceNotFoundException("image not not found [" + key + "]");
    }
}
