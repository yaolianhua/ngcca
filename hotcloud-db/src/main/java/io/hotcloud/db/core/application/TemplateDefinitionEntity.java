package io.hotcloud.db.core.application;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "template_definition")
@RedisHash("template_definition")
@Getter
@Setter
public class TemplateDefinitionEntity extends AbstractEntity {

    @Indexed(unique = true)
    @org.springframework.data.redis.core.index.Indexed
    private String name;

    private String version;

    private String logo;

    private String shortDesc;

    private String description;
}
