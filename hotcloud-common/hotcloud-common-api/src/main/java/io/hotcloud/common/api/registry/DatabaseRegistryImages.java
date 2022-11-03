package io.hotcloud.common.api.registry;

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
}
