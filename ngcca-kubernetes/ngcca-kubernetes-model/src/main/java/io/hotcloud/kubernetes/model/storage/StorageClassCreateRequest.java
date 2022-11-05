package io.hotcloud.kubernetes.model.storage;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StorageClassCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    /**
     * AllowVolumeExpansion shows whether the storage class allow volume expand
     */
    private Boolean allowVolumeExpansion = true;

    /**
     * VolumeBindingMode indicates how PersistentVolumeClaims should be
     * provisioned and bound. When unset, VolumeBindingImmediate is used. This
     * field is only honored by servers that enable the VolumeScheduling feature
     */
    private String volumeBindingMode = "WaitForFirstConsumer";

    /**
     * Dynamically provisioned PersistentVolumes of this storage class are created
     * with this reclaimPolicy. Defaults to Delete.
     */
    private String reclaimPolicy = "Delete";

    /**
     * Provisioner indicates the type of the provisioner
     */
    private String provisioner = "kubernetes.io/no-provisioner";

    /**
     * Parameters holds the parameters for the provisioner that should create
     * volumes of this storage class
     */
    private Map<String, String> parameters;

    /**
     * Dynamically provisioned PersistentVolumes of this storage class are created
     * with these mountOptions, e.g. ["ro", "soft"]. Not validated - mount of the
     * PVs will simply fail if one is invalid
     */
    private List<String> mountOptions;
}
