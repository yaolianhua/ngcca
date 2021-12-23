package io.hotcloud.core.kubernetes.volume;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private PersistentVolumeSpec spec = new PersistentVolumeSpec();
}
