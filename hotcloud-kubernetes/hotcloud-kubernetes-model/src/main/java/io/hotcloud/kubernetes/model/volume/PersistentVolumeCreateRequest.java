package io.hotcloud.kubernetes.model.volume;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private PersistentVolumeSpec spec = new PersistentVolumeSpec();
}
