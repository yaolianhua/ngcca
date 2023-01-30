package io.hotcloud.kubernetes.model.storage;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PersistentVolumeClaimCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    @Valid
    private PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
}
