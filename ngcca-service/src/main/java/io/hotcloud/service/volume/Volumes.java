package io.hotcloud.service.volume;

import io.hotcloud.db.entity.VolumeEntity;
import io.hotcloud.db.model.Cluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Volumes {

    private String id;
    private String name;
    private Cluster cluster;

    private Integer gigabytes;

    private String type;

    private String createUsername;

    private String persistentVolume;

    private String persistentVolumeClaim;

    private String namespace;

    private boolean used;

    private Instant createdAt;

    public static Volumes toVolumes(VolumeEntity entity) {
        return Volumes.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cluster(entity.getCluster())
                .gigabytes(entity.getGigabytes())
                .type(entity.getType())
                .createUsername(entity.getCreateUsername())
                .persistentVolume(entity.getPersistentVolume())
                .persistentVolumeClaim(entity.getPersistentVolumeClaim())
                .namespace(entity.getNamespace())
                .used(entity.isUsed())
                .createdAt(entity.getCreatedAt()).build();
    }

}
