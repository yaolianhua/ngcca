package io.hotcloud.server.registry;

import org.springframework.util.StringUtils;

public interface DatabaseRegistryImages {

    /**
     * Put image to containers
     *
     * @param key   image name e.g. minio
     * @param value image url e.g. harbor.local:5000/library/minio:latest
     */
    void put(String key, String value);

    /**
     * get image from containers
     *
     * @param key image name e.g. minio
     * @return image url e.g. harbor.local:5000/library/minio:latest
     */
    String get(String key);
    /**
     * get image from containers
     *
     * @param key image name e.g. minio
     * @param defaultValue the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key
     * @return image url e.g. harbor.local:5000/library/minio:latest
     */
    default String getOrDefault(String key, String defaultValue){
        if (!StringUtils.hasText(key)) {
            return defaultValue;
        }
        String v = get(key);
        if (!StringUtils.hasText(v)){
            return defaultValue;
        }

        return v;
    }
}
