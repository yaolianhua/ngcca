package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

@Data
public class LocalVolume {

    public static final String DEFAULT_PATH = "/data";

    /**
     * Filesystem type to mount. It applies only when the Path is a block device.
     * Must be a filesystem type supported by the host operating system. Ex.
     * "ext4", "xfs", "ntfs". The default value is to auto-select a fileystem if
     * unspecified
     */
    private String fsType;

    /**
     * The full path to the volume on the node. It can be either a directory or
     * block device (disk, partition, ...).
     */
    private String path = DEFAULT_PATH;
}
