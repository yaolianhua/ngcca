package io.hotcloud.service.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TemplateVariables {
    /**
     * template image e.g. 127.0.0.1/template/minio:latest
     */
    private String imageUrl;
    private String username;
    private String namespace;
    private String storageNode;
}
