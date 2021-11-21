package io.hotcloud.core.kubernetes.volume;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeCreateParams {

    private PersistentVolumeMetadata metadata = new PersistentVolumeMetadata();

    private PersistentVolumeSpec spec = new PersistentVolumeSpec();
}
