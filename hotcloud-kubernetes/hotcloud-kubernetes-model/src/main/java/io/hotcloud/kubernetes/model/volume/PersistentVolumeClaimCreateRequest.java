package io.hotcloud.kubernetes.model.volume;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    @Valid
    private PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
}
