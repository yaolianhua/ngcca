package io.hotcloud.common.api.registry;

import lombok.Data;

@Data
public class DatabaseRegistryImage {
    /**
     * registry image name
     * <ul>
     *   <li>minio
     *   <li>kaniko
     *   <li>...
     * </ul>
     */
    private String name;

    /**
     * registry image value e.g. 127.0.0.1:5000/app/minio:latest
     */
    private String value;
}
