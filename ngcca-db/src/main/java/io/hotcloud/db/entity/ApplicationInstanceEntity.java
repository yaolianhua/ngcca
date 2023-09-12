package io.hotcloud.db.entity;

import io.hotcloud.db.AbstractEntity;
import io.hotcloud.db.model.ApplicationInstanceSource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;


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
    private int progress;

    private String namespace;
    private String service;

    private String targetPorts;
    private String servicePorts;
    private String nodePorts;

    private boolean deleted;
    private boolean canHttp;
    private String host;
    private String ingress;
    private String loadBalancerIngressIp;

    private ApplicationInstanceSource source;
    private String yaml;

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Map<String, String> envs = new HashMap<>();

    private boolean success;
    private String message;

}
