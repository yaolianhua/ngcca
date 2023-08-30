package io.hotcloud.service.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDefinition {

    private String id;
    /**
     * {@link Template}
     */
    private String name;

    private String version;

    private String logo;

    private String shortDesc;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
