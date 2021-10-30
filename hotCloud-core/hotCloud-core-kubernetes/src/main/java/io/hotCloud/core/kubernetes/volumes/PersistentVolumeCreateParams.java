package io.hotCloud.core.kubernetes.volumes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeCreateParams {

    private PersistentVolumeMetadata metadata = new PersistentVolumeMetadata();

    private PersistentVolumeSpec spec = new PersistentVolumeSpec();
}
