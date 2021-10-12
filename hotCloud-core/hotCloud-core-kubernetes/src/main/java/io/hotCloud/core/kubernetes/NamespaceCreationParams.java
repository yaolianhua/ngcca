package io.hotCloud.core.kubernetes;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NamespaceCreationParams {

    @NotNull(message = "Namespace metadata is null")
    private NamespaceMetadata metadata;
}
