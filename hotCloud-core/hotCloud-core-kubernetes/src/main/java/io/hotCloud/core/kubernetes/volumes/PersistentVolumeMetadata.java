package io.hotCloud.core.kubernetes.volumes;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeMetadata {

    @NotBlank(message = "persistentVolume name is empty")
    private String name;

    private String namespace;

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();

}
