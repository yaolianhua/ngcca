package io.hotcloud.buildpack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class StorageResourceList {

    private String namespace;
    private String storageClass;
    private String persistentVolume;
    private String persistentVolumeClaim;
    private Integer sizeGb;

    @JsonProperty("yaml")
    private String resourceListYaml;

    public StorageResourceList(String namespace, String storageClass, String persistentVolume, String persistentVolumeClaim, Integer sizeGb, String resourceListYaml) {
        this.namespace = namespace;
        this.storageClass = storageClass;
        this.persistentVolume = persistentVolume;
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.sizeGb = sizeGb;
        this.resourceListYaml = resourceListYaml;
    }

    public StorageResourceList() {
    }
}
