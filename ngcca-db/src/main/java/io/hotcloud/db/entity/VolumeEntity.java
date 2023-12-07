package io.hotcloud.db.entity;

import io.hotcloud.db.AbstractEntity;
import io.hotcloud.db.model.Cluster;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "volumes")
@Getter
@Setter
public class VolumeEntity extends AbstractEntity {

    private String name;

    private Integer gigabytes;

    private String type;

    private Cluster cluster;

    private String createUsername;

    private String persistentVolume;

    private String persistentVolumeClaim;

    private String namespace;

    private boolean used;

}
