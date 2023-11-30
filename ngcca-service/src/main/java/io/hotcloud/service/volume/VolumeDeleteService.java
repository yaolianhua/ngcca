package io.hotcloud.service.volume;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.db.entity.VolumeEntity;
import io.hotcloud.db.entity.VolumeRepository;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClaimClient;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClient;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class VolumeDeleteService {

    private final PersistentVolumeClient persistentVolumeClient;
    private final PersistentVolumeClaimClient persistentVolumeClaimClient;

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final VolumeRepository volumeRepository;


    public void delete(String id) {
        Optional<VolumeEntity> optionalVolume = volumeRepository.findById(id);
        if (optionalVolume.isEmpty()) {
            throw new PlatformException("volume [" + id + "] not  found", 404);
        }

        VolumeEntity volume = optionalVolume.get();
        if (volume.isUsed()) {
            throw new PlatformException("volume is in use");
        }
        String namespace = volume.getNamespace();
        String persistentVolume = volume.getPersistentVolume();
        String persistentVolumeClaim = volume.getPersistentVolumeClaim();

        KubernetesCluster cluster = databasedKubernetesClusterService.findById(CommonConstant.DEFAULT_CLUSTER_ID);

        try {
            persistentVolumeClient.delete(cluster.getAgentUrl(), persistentVolume);
            persistentVolumeClaimClient.delete(cluster.getAgentUrl(), namespace, persistentVolumeClaim);
        } catch (Exception e) {
            throw new PlatformException("delete volume error: " + e.getMessage());
        }

        volumeRepository.deleteById(id);
    }

    public void deleteByUsername(String username) {
        List<VolumeEntity> volumeEntities = volumeRepository.findByCreateUsername(username);
        for (VolumeEntity entity : volumeEntities) {
            try {
                this.delete(entity.getId());
            } catch (Exception e) {
                Log.error(this, username, Event.EXCEPTION, "delete user volume error. " + e.getMessage());
            }
        }
    }

    public void delete() {
        Iterable<VolumeEntity> volumeEntityIterable = volumeRepository.findAll();
        List<VolumeEntity> entities = StreamSupport.stream(volumeEntityIterable.spliterator(), false)
                .toList();
        for (VolumeEntity entity : entities) {
            try {
                this.delete(entity.getId());
            } catch (Exception e) {
                Log.error(this, null, Event.EXCEPTION, "delete volume error. " + e.getMessage());
            }
        }
    }


}
