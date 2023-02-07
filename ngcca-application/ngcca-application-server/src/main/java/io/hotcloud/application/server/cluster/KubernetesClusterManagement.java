package io.hotcloud.application.server.cluster;

import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import io.hotcloud.db.core.cluster.KubernetesClusterEntity;
import io.hotcloud.db.core.cluster.KubernetesClusterRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Component
public class KubernetesClusterManagement {

    private final KubernetesClusterRepository kubernetesClusterRepository;

    public KubernetesClusterManagement(KubernetesClusterRepository kubernetesClusterRepository) {
        this.kubernetesClusterRepository = kubernetesClusterRepository;
    }

    public void save(KubernetesCluster info) {

        if (StringUtils.hasText(info.getId())) {
            KubernetesClusterEntity fetched = kubernetesClusterRepository.findById(info.getId()).orElseThrow(() -> new NGCCAResourceNotFoundException("Record not found [" + info.getId() + "]"));
            fetched.setName(info.getName());
            fetched.setMasters(info.getMasters());
            fetched.setNodes(info.getNodes());
            fetched.setModifiedAt(LocalDateTime.now());

            kubernetesClusterRepository.save(fetched);
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


    public KubernetesCluster one(String id) {
        KubernetesClusterEntity entity = kubernetesClusterRepository.findById(id).orElse(null);
        return Objects.isNull(entity) ? null : entity.toT(KubernetesCluster.class);
    }
}
