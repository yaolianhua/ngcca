package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackStorageResourceRequest {

    /**
     * In which namespace the pvc will be created
     */
    private String namespace;
    /**
     * The name pv will be created
     */
    @Nullable
    private String persistentVolume;
    /**
     * The name pvc will be created
     */
    @Nullable
    private String persistentVolumeClaim;
    /**
     * The capacity of pv. unit is GB
     */
    @Nullable
    @JsonProperty("size")
    private Integer sizeGb;

    public BuildPackStorageResourceRequest(String namespace, @Nullable String persistentVolume, @Nullable String persistentVolumeClaim, @Nullable Integer sizeGb) {
        this.namespace = namespace;
        this.persistentVolume = persistentVolume;
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.sizeGb = sizeGb;
    }

    public BuildPackStorageResourceRequest() {
    }
}
