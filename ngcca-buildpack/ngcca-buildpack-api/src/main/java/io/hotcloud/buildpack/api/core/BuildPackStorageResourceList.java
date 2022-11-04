package io.hotcloud.buildpack.api.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
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
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();

    /**
     * Generated pv/pvc resource list yaml
     */
    @JsonProperty("yaml")
    private String resourceListYaml;

    public BuildPackStorageResourceList() {
    }
}
