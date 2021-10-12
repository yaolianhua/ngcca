package io.hotCloud.core.kubernetes;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class ServiceCreationParams {

    @NotNull(message = "service metadata is null")
    private ServiceMetadata serviceMetadata;
    @NotNull(message = "service spec is null")
    private ServiceSpec serviceSpec;
}
