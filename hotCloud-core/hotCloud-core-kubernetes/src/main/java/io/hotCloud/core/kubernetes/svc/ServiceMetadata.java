package io.hotCloud.core.kubernetes.svc;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class ServiceMetadata extends V1ObjectMeta {

    @NotBlank(message = "namespace is empty")
    private String namespace;

    @NotBlank(message = "service name is empty")
    private String name;

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();

}
