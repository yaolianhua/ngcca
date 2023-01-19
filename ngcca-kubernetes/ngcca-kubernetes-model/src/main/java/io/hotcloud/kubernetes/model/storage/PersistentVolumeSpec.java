package io.hotcloud.kubernetes.model.storage;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeSpec {

    @NotEmpty(message = "accessModes is empty")
    private List<String> accessModes = new ArrayList<>();
    @NotEmpty(message = "capacity is empty")
    private Map<String, String> capacity = new HashMap<>();

    private HostPathVolume hostPath;

    private List<String> mountOptions = new ArrayList<>();

    private NFSVolume nfs;

    private VolumeNodeAffinity nodeAffinity = new VolumeNodeAffinity();

    private ReclaimPolicy persistentVolumeReclaimPolicy = ReclaimPolicy.Recycle;

    private String storageClassName;

    private ClaimRef claimRef = new ClaimRef();

    private VolumeMode volumeMode = VolumeMode.Filesystem;

    public enum VolumeMode {
        //
        Filesystem, Block
    }

    public enum ReclaimPolicy {
        //
        Retain, Recycle, Delete
    }

    @Data
    public static class ClaimRef {
        private String name;
        private String namespaces;
    }
}
