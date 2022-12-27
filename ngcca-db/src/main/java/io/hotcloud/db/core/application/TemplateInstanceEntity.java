package io.hotcloud.db.core.application;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "template_instance")
@CompoundIndex(
        name = "user_name_idx",
        def = "{'user': 1, 'name': 1}",
        unique = true
)
@Getter
@Setter
public class TemplateInstanceEntity extends AbstractEntity {

    @Indexed
    private String user;
    @Indexed
    private String name;
    @Indexed(unique = true)
    private String uuid;
    private String version;
    private int progress;

    private String namespace;
    private String host;

    private String protocol;

    private String service;

    private String targetPorts;
    private String httpPort;

    private String nodePorts;

    private String yaml;
    private String ingress;

    private boolean success;

    private String message;
}
