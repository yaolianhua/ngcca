package io.hotcloud.core.kubernetes.volume;

import io.hotcloud.core.kubernetes.LabelSelector;
import io.hotcloud.core.kubernetes.Resources;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimSpec {

    private List<String> accessModes = new ArrayList<>();

    private LabelSelector selector = new LabelSelector();

    private String storageClassName;

    private Resources resources = new Resources();

    private VolumeMode volumeMode = VolumeMode.Filesystem;

    private String volumeName;

    public enum VolumeMode {
        //
        Filesystem, Block
    }

}
