package io.hotCloud.core.kubernetes.volumes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimCreateParams {

    private PersistentVolumeClaimMetadata metadata = new PersistentVolumeClaimMetadata();

    private PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
}
