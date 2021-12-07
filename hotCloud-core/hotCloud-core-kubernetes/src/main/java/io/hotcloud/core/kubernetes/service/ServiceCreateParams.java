package io.hotcloud.core.kubernetes.service;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class ServiceCreateParams {

    @NotNull(message = "service metadata is null")
    private ObjectMetadata serviceMetadata;
    @NotNull(message = "service spec is null")
    private DefaultServiceSpec serviceSpec;
}
