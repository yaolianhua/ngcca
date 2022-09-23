package io.hotcloud.application.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.application.api.core.ApplicationInstanceSource;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.exception.HotCloudResourceNotFoundException;
import io.hotcloud.db.core.application.ApplicationInstanceEntity;
import io.hotcloud.db.core.application.ApplicationInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationInstanceServiceImpl implements ApplicationInstanceService {

    private final ApplicationInstanceRepository applicationInstanceRepository;
    private final ObjectMapper objectMapper;

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
        ApplicationInstanceEntity entity = applicationInstanceRepository.findById(id).orElseThrow(() -> new HotCloudResourceNotFoundException("Can not found application instance [" + id + "]"));
        return toApplicationInstance(entity);
    }

    @Override
    public void delete(String id) {
        ApplicationInstanceEntity fetched = applicationInstanceRepository.findById(id).orElse(null);
        if (Objects.isNull(fetched)){
            return;
        }

        fetched.setDeleted(true);
        applicationInstanceRepository.save(fetched);
    }

    @Override
    public ApplicationInstance saveOrUpdate(ApplicationInstance applicationInstance) {
        String id = applicationInstance.getId();
        if (StringUtils.hasText(id)) {
            ApplicationInstanceEntity fetched = applicationInstanceRepository.findById(id).orElseThrow(() -> new HotCloudResourceNotFoundException("Application instance not found [" + id + "]"));
            fetched.setMessage(applicationInstance.getMessage());
            fetched.setNodePorts(applicationInstance.getNodePorts());
            fetched.setIngress(applicationInstance.getIngress());
            fetched.setSuccess(applicationInstance.isSuccess());
            fetched.setDeleted(applicationInstance.isDeleted());
            fetched.setService(applicationInstance.getService());
            fetched.setServicePorts(applicationInstance.getServicePorts());
            fetched.setTargetPorts(applicationInstance.getTargetPorts());
            fetched.setBuildPackId(applicationInstance.getBuildPackId());
            fetched.setHost(applicationInstance.getHost());
            fetched.setName(applicationInstance.getName());
            fetched.setCanHttp(applicationInstance.isCanHttp());
            fetched.setReplicas(applicationInstance.getReplicas());

            fetched.setModifiedAt(LocalDateTime.now());

            ApplicationInstanceEntity updated = applicationInstanceRepository.save(fetched);
            return toApplicationInstance(updated);
        }

        ApplicationInstanceEntity entity = (ApplicationInstanceEntity) new ApplicationInstanceEntity().copyToEntity(applicationInstance);
        entity.setEnvs(writeJson(applicationInstance.getEnvs()));
        entity.setSource(writeJson(applicationInstance.getSource()));
        entity.setCreatedAt(LocalDateTime.now());

        ApplicationInstanceEntity saved = applicationInstanceRepository.save(entity);

        return toApplicationInstance(saved);
    }

    @SuppressWarnings("unchecked")
    private ApplicationInstance toApplicationInstance(ApplicationInstanceEntity entity) {
        if (Objects.isNull(entity)){
            return null;
        }
        return ApplicationInstance.builder()
                .id(entity.getId())
                .buildPackId(entity.getBuildPackId())
                .user(entity.getUser())
                .name(entity.getName())
                .namespace(entity.getNamespace())
                .service(entity.getService())
                .targetPorts(entity.getTargetPorts())
                .host(entity.getHost())
                .servicePorts(entity.getServicePorts())
                .ingress(entity.getIngress())
                .nodePorts(entity.getNodePorts())
                .success(entity.isSuccess())
                .canHttp(entity.isCanHttp())
                .deleted(entity.isDeleted())
                .replicas(entity.getReplicas())
                .source(readT(entity.getSource(), ApplicationInstanceSource.class))
                .envs(readT(entity.getEnvs(), Map.class))
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }

    private <T> String writeJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new HotCloudException("Write value error. " + e.getCause().getMessage());
        }
    }

    private <T> T readT(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new HotCloudException("Read value error. " + e.getCause().getMessage());
        }
    }
}
