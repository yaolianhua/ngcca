package io.hotcloud.db.core.application;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "application_instance")
@CompoundIndex(
        name = "user_name_idx",
        def = "{'user': 1, 'name': 1}",
        unique = false
)
@Getter
@Setter
public class ApplicationInstanceEntity extends AbstractEntity {

    private String buildPackId;
    @Indexed
    private String user;
    @Indexed
    private String name;

    private String namespace;
    private String service;

    private String targetPorts;
    private String servicePorts;
    private String nodePorts;

    private boolean deleted;
    private boolean canHttp;
    private String host;
    private String ingress;

    private String source;

    @Builder.Default
    private Integer replicas = 1;
    private String envs;

    private boolean success;
    private String message;

}
