package io.hotcloud.web.template;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class TemplateDefinition {

    private String id;

    private String name;

    private String version;

    private String logo;

    private String shortDesc;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
