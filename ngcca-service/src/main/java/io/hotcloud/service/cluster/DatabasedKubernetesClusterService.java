package io.hotcloud.service.cluster;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.db.entity.KubernetesClusterEntity;
import io.hotcloud.db.entity.KubernetesClusterRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
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

        if (StringUtils.hasText(info.getId())) {
            Optional<KubernetesClusterEntity> optionalKubernetesCluster = kubernetesClusterRepository.findById(info.getId());
            if (optionalKubernetesCluster.isEmpty()) {
                throw new PlatformException("cluster not found: id=" + info.getId());
            }
            KubernetesClusterEntity fetched = optionalKubernetesCluster.get();
            fetched.setMasters(info.getMasters());
            fetched.setNodes(info.getNodes());
            fetched.setAgentUrl(info.getAgentUrl());
            fetched.setModifiedAt(Instant.now());
            kubernetesClusterRepository.save(fetched);
            return;
        }
        if (!StringUtils.hasText(info.getName())) {
            throw new PlatformException("cluster name is null");
        }
        if (!StringUtils.hasText(info.getAgentUrl())) {
            throw new PlatformException("cluster agent url is null");
        }

        KubernetesClusterEntity entity = (KubernetesClusterEntity) new KubernetesClusterEntity().toE(info);
        entity.setCreatedAt(Instant.now());
        kubernetesClusterRepository.save(entity);
    }

    public List<KubernetesCluster> list() {
        return StreamSupport.stream(kubernetesClusterRepository.findAll().spliterator(), false)
                .map(e -> e.toT(KubernetesCluster.class))
                .toList();
    }


    public KubernetesCluster findByName(String name) {
        KubernetesClusterEntity entity = kubernetesClusterRepository.findByName(name).orElse(null);
        return Objects.isNull(entity) ? null : entity.toT(KubernetesCluster.class);
    }

    public KubernetesCluster findById(String name) {
        KubernetesClusterEntity entity = kubernetesClusterRepository.findByName(name).orElse(null);
        return Objects.isNull(entity) ? null : entity.toT(KubernetesCluster.class);
    }
}
