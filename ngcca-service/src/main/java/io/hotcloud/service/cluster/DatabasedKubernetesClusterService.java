package io.hotcloud.service.cluster;

import io.hotcloud.db.entity.KubernetesClusterEntity;
import io.hotcloud.db.entity.KubernetesClusterRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class DatabasedKubernetesClusterService {

    private final KubernetesClusterRepository kubernetesClusterRepository;

    public DatabasedKubernetesClusterService(KubernetesClusterRepository kubernetesClusterRepository) {
        this.kubernetesClusterRepository = kubernetesClusterRepository;
    }

    public void saveOrUpdate(KubernetesCluster info) {

        Optional<KubernetesClusterEntity> optional = kubernetesClusterRepository.findByName(info.getName());
        if (optional.isPresent()) {
            KubernetesClusterEntity fetched = optional.get();
            fetched.setMasters(info.getMasters());
            fetched.setNodes(info.getNodes());
            fetched.setModifiedAt(LocalDateTime.now());
            kubernetesClusterRepository.save(fetched);
            return;
        }

        KubernetesClusterEntity entity = (KubernetesClusterEntity) new KubernetesClusterEntity().toE(info);
        entity.setCreatedAt(LocalDateTime.now());
        kubernetesClusterRepository.save(entity);
    }

    public List<KubernetesCluster> list() {
        return StreamSupport.stream(kubernetesClusterRepository.findAll().spliterator(), false)
                .map(e -> e.toT(KubernetesCluster.class))
                .toList();
    }


    public KubernetesCluster findOne(String name) {
        KubernetesClusterEntity entity = kubernetesClusterRepository.findByName(name).orElse(null);
        return Objects.isNull(entity) ? null : entity.toT(KubernetesCluster.class);
    }
}
