package io.hotcloud.kubernetes.model.volume;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.Resources;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimSpec {

    @NotEmpty(message = "accessModes is empty")
    private List<String> accessModes = new ArrayList<>();

    private LabelSelector selector = new LabelSelector();

    private String storageClassName;

    @Valid
    private Resources resources = new Resources();

    private VolumeMode volumeMode = VolumeMode.Filesystem;

    private String volumeName;

    public enum VolumeMode {
        //
        Filesystem, Block
    }

}
