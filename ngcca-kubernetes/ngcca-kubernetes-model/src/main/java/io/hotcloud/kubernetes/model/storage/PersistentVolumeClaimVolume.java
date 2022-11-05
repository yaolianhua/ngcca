package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimVolume {

    private String claimName;
    private Boolean readOnly;
}
