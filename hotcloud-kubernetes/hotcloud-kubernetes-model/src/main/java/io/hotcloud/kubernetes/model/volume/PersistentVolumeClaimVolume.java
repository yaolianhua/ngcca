package io.hotcloud.kubernetes.model.volume;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimVolume {

    private String claimName;
    private Boolean readOnly;
}
