package io.hotcloud.kubernetes.model.network;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class ServiceCreateRequest {

    private ObjectMetadata serviceMetadata = new ObjectMetadata();
    @Valid
    private DefaultServiceSpec serviceSpec = new DefaultServiceSpec();
}
