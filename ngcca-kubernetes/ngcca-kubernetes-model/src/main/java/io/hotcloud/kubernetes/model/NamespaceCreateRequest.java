package io.hotcloud.kubernetes.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NamespaceCreateRequest {

    @NotNull(message = "Namespace metadata is null")
    private ObjectMetadata metadata;
}
