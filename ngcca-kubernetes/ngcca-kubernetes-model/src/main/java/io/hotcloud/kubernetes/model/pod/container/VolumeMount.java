package io.hotcloud.kubernetes.model.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class VolumeMount {
    private String mountPath;
    private String name;
    private Boolean readOnly;
    private String subPath;
    private String subPathExpr;
    private String mountPropagation;

    public static VolumeMount of(String name, String mountPath, Boolean readOnly) {
        VolumeMount volumeMount = new VolumeMount();

        volumeMount.setMountPath(mountPath);
        volumeMount.setName(name);
        volumeMount.setReadOnly(readOnly);

        return volumeMount;
    }
}
