package io.hotcloud.service.application;

import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.ResourceNotFoundException;
import io.hotcloud.db.entity.ApplicationInstanceEntity;
import io.hotcloud.db.entity.ApplicationInstanceRepository;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ApplicationInstanceServiceImpl implements ApplicationInstanceService {

    private final ApplicationInstanceRepository applicationInstanceRepository;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    @Override
    public List<ApplicationInstance> find(String user, String name) {
        List<ApplicationInstanceEntity> entities = applicationInstanceRepository.findByNameAndUser(name, user);
        return entities.stream().map(this::toApplicationInstance).collect(Collectors.toList());
    }

    @Override
    public ApplicationInstance findActiveSucceed(String user, String name) {
        List<ApplicationInstance> applicationInstances = find(user, name);
        return applicationInstances.stream().filter(ApplicationInstance::isSuccess)
                .filter(e -> !e.isDeleted())
                .findFirst()
                .orElse(null);

    }

    @Override
    public ApplicationInstance findOne(String id) {
        ApplicationInstanceEntity entity = applicationInstanceRepository.findById(id).orElse(null);
        return entity == null ? null : toApplicationInstance(entity);
    }

    @Override
    public List<ApplicationInstance> findAll() {
        Iterable<ApplicationInstanceEntity> iterable = applicationInstanceRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(this::toApplicationInstance)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationInstance> findAll(String user) {
        return applicationInstanceRepository.findByUser(user)
                .stream()
                .map(this::toApplicationInstance)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        ApplicationInstanceEntity fetched = applicationInstanceRepository.findById(id).orElse(null);
        if (Objects.isNull(fetched)) {
            return;
        }

        fetched.setDeleted(true);
        applicationInstanceRepository.save(fetched);
    }

    @Override
    public ApplicationInstance saveOrUpdate(ApplicationInstance applicationInstance) {
        String id = applicationInstance.getId();
        if (StringUtils.hasText(id)) {
            ApplicationInstanceEntity fetched = applicationInstanceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application instance not found [" + id + "]"));
            fetched.setMessage(applicationInstance.getMessage());
            fetched.setNodePorts(applicationInstance.getNodePorts());
            fetched.setIngress(applicationInstance.getIngress());
            fetched.setLoadBalancerIngressIp(applicationInstance.getLoadBalancerIngressIp());
            fetched.setSuccess(applicationInstance.isSuccess());
            fetched.setDeleted(applicationInstance.isDeleted());
            fetched.setService(applicationInstance.getService());
            fetched.setServicePorts(applicationInstance.getServicePorts());
            fetched.setTargetPorts(applicationInstance.getTargetPorts());
            fetched.setBuildPackId(applicationInstance.getBuildPackId());
            fetched.setHost(applicationInstance.getHost());
            fetched.setName(applicationInstance.getName());
            fetched.setProgress(applicationInstance.getProgress());
            fetched.setEnableIngressAccess(applicationInstance.isEnableIngressAccess());
            fetched.setReplicas(applicationInstance.getReplicas());
            fetched.setYaml(applicationInstance.getYaml());
            fetched.setClusterId(applicationInstance.getClusterId());

            fetched.setModifiedAt(Instant.now());

            ApplicationInstanceEntity updated = applicationInstanceRepository.save(fetched);
            return toApplicationInstance(updated);
        }

        ApplicationInstanceEntity entity = (ApplicationInstanceEntity) new ApplicationInstanceEntity().toE(applicationInstance);
        entity.setEnvs(applicationInstance.getEnvs());
        entity.setSource(applicationInstance.getSource());
        entity.setResource(applicationInstance.getResource());
        entity.setCreatedAt(Instant.now());

        ApplicationInstanceEntity saved = applicationInstanceRepository.save(entity);

        return toApplicationInstance(saved);
    }

    public ApplicationInstance toApplicationInstance(ApplicationInstanceEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        String clusterId = StringUtils.hasText(entity.getClusterId()) ? entity.getClusterId() : CommonConstant.DEFAULT_CLUSTER_ID;
        KubernetesCluster cluster = databasedKubernetesClusterService.findById(clusterId);
        return ApplicationInstance.builder()
                .id(entity.getId())
                .clusterId(entity.getClusterId())
                .cluster(cluster)
                .buildPackId(entity.getBuildPackId())
                .user(entity.getUser())
                .name(entity.getName())
                .progress(entity.getProgress())
                .namespace(entity.getNamespace())
                .service(entity.getService())
                .targetPorts(entity.getTargetPorts())
                .host(entity.getHost())
                .servicePorts(entity.getServicePorts())
                .ingress(entity.getIngress())
                .loadBalancerIngressIp(entity.getLoadBalancerIngressIp())
                .nodePorts(entity.getNodePorts())
                .success(entity.isSuccess())
                .enableIngressAccess(entity.isEnableIngressAccess())
                .deleted(entity.isDeleted())
                .replicas(entity.getReplicas())
                .source(entity.getSource())
                .yaml(entity.getYaml())
                .envs(entity.getEnvs())
                .resource(entity.getResource())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
