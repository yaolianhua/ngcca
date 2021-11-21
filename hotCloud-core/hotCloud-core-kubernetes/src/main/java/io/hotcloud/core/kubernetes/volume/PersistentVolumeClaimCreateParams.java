package io.hotcloud.core.kubernetes.volume;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimCreateParams {

    private PersistentVolumeClaimMetadata metadata = new PersistentVolumeClaimMetadata();

    private PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
}
