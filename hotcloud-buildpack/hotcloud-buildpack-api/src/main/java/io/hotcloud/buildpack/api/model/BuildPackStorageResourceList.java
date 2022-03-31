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

    /**
     * In which namespace the pvc be created
     */
    private String namespace;
    /**
     * StorageClass name
     */
    private String storageClass;
    /**
     * The name pv be created
     */
    private String persistentVolume;
    /**
     * The name pvc be created
     */
    private String persistentVolumeClaim;
    /**
     * The capacity of pv.
     */
    private String capacity;

    /**
     * Generated pv/pvc resource list yaml
     */
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
