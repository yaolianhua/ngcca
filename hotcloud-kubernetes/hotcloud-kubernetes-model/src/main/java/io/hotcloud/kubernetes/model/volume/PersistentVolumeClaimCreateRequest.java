package io.hotcloud.kubernetes.model.volume;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
}
