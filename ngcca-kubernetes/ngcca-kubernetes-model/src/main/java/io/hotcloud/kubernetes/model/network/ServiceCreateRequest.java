package io.hotcloud.kubernetes.model.network;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class ServiceCreateRequest {

    private ObjectMetadata serviceMetadata = new ObjectMetadata();
    @Valid
    private DefaultServiceSpec serviceSpec = new DefaultServiceSpec();
}
