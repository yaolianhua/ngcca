package io.hotCloud.core.kubernetes;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Builder;
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
@Builder
public class NamespaceMetadata extends V1ObjectMeta {

    @NotBlank(message = "Namespace's name is empty")
    private String name;

    @Builder.Default
    private Map<String, String> labels = new HashMap<>();

    @Builder.Default
    private Map<String, String> annotations = new HashMap<>();

}
