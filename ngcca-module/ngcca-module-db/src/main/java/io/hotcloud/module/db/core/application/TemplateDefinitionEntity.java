package io.hotcloud.module.db.core.application;

import io.hotcloud.module.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "template_definition")
@Getter
@Setter
public class TemplateDefinitionEntity extends AbstractEntity {

    @Indexed(unique = true)
    private String name;

    private String version;

    private String logo;

    private String shortDesc;

    private String description;
}
