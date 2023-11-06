package io.hotcloud.service.template;

import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.ResourceNotFoundException;
import io.hotcloud.db.entity.TemplateInstanceEntity;
import io.hotcloud.db.entity.TemplateInstanceRepository;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class TemplateInstanceServiceImpl implements TemplateInstanceService {

    private final TemplateInstanceRepository templateInstanceRepository;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    public TemplateInstanceServiceImpl(TemplateInstanceRepository templateInstanceRepository,
                                       DatabasedKubernetesClusterService databasedKubernetesClusterService) {
        this.templateInstanceRepository = templateInstanceRepository;
        this.databasedKubernetesClusterService = databasedKubernetesClusterService;
    }

    @Override
    public TemplateInstance saveOrUpdate(TemplateInstance instance) {
        Assert.notNull(instance, "Application instance body is null");

        if (StringUtils.hasText(instance.getId())) {
            TemplateInstanceEntity find = templateInstanceRepository.findById(instance.getId()).orElseThrow(() -> new ResourceNotFoundException("instance template not found [" + instance.getId() + "]"));

            find.setModifiedAt(Instant.now());
            find.setSuccess(instance.isSuccess());
            find.setMessage(instance.getMessage());
            find.setNodePorts(instance.getNodePorts());
            find.setProgress(instance.getProgress());
            find.setLoadBalancerIngressIp(instance.getLoadBalancerIngressIp());
            TemplateInstanceEntity updated = templateInstanceRepository.save(find);

            return build(updated);
        }

        TemplateInstanceEntity existEntity = templateInstanceRepository.findByUserAndName(instance.getUser(), instance.getName());
        if (existEntity != null) {
            throw new IllegalStateException("instance template [" + existEntity.getId() + "] already exist for user [" + existEntity.getUser() + "]");
        }

        TemplateInstanceEntity entity = (TemplateInstanceEntity) new TemplateInstanceEntity().toE(instance);
        entity.setCreatedAt(Instant.now());

        TemplateInstanceEntity saved = templateInstanceRepository.save(entity);

        return build(saved);
    }

    @Override
    public TemplateInstance findOne(String id) {
        TemplateInstanceEntity entity = templateInstanceRepository.findById(id).orElse(null);
        return entity == null ? null : build(entity);
    }

    @Override
    public TemplateInstance findByUuid(String uuid) {
        TemplateInstanceEntity entity = templateInstanceRepository.findByUuid(uuid);
        return entity == null ? null : build(entity);
    }

    @Override
    public List<TemplateInstance> findAll() {
        Iterable<TemplateInstanceEntity> entityIterable = templateInstanceRepository.findAll();
        return StreamSupport.stream(entityIterable.spliterator(), false)
                .map(this::build)
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateInstance> findAll(String user) {
        return templateInstanceRepository.findByUser(user)
                .stream()
                .map(this::build)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        templateInstanceRepository.deleteById(id);
    }

    private TemplateInstance build(TemplateInstanceEntity entity) {
        final String clusterId = StringUtils.hasText(entity.getClusterId()) ? entity.getClusterId() : CommonConstant.DEFAULT_CLUSTER_ID;
        KubernetesCluster cluster = databasedKubernetesClusterService.findById(clusterId);
        TemplateInstance result = entity.toT(TemplateInstance.class);
        result.setCluster(cluster);

        return result;
    }
}
