package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackStorageResourceList {

    private String namespace;
    private String storageClass;
    private String persistentVolume;
    private String persistentVolumeClaim;
    private String capacity;

    @JsonProperty("yaml")
    private String resourceListYaml;

    public BuildPackStorageResourceList(String namespace, String storageClass, String persistentVolume, String persistentVolumeClaim, String capacity, String resourceListYaml) {
        this.namespace = namespace;
        this.storageClass = storageClass;
        this.persistentVolume = persistentVolume;
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.capacity = capacity;
        this.resourceListYaml = resourceListYaml;
    }

    public BuildPackStorageResourceList() {
    }
}
