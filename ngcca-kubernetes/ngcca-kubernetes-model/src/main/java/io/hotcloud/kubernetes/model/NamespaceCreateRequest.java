package io.hotcloud.kubernetes.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NamespaceCreateRequest {

    @NotNull(message = "Namespace metadata is null")
    private ObjectMetadata metadata;
}
