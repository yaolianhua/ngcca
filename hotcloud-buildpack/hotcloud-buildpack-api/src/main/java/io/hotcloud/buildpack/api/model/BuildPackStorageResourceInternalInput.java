package io.hotcloud.buildpack.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class BuildPackStorageResourceInternalInput {

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
     * The capacity of pv.
     */
    @Nullable
    private String capacity;

    /**
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();

    public BuildPackStorageResourceInternalInput() {
    }
}
