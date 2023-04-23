package io.hotcloud.server.application.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import io.hotcloud.module.application.core.ApplicationInstanceSource;
import io.hotcloud.module.db.core.application.ApplicationInstanceEntity;
import io.hotcloud.module.db.core.application.ApplicationInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        ApplicationInstanceEntity entity = applicationInstanceRepository.findById(id).orElse(null);
        return entity == null ? null : toApplicationInstance(entity);
    }

    @Override
    public List<ApplicationInstance> findAll() {
        Iterable<ApplicationInstanceEntity> iterable = applicationInstanceRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(e -> e.toT(ApplicationInstance.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationInstance> findAll(String user) {
        return applicationInstanceRepository.findByUser(user)
                .stream()
                .map(e -> e.toT(ApplicationInstance.class))
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
            ApplicationInstanceEntity fetched = applicationInstanceRepository.findById(id).orElseThrow(() -> new NGCCAResourceNotFoundException("Application instance not found [" + id + "]"));
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

        ApplicationInstanceEntity entity = (ApplicationInstanceEntity) new ApplicationInstanceEntity().toE(applicationInstance);
        entity.setEnvs(writeJson(applicationInstance.getEnvs()));
        entity.setSource(writeJson(applicationInstance.getSource()));
        entity.setCreatedAt(LocalDateTime.now());

        ApplicationInstanceEntity saved = applicationInstanceRepository.save(entity);

        return toApplicationInstance(saved);
    }

    @SuppressWarnings("unchecked")
    private ApplicationInstance toApplicationInstance(ApplicationInstanceEntity entity) {
        if (Objects.isNull(entity)) {
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
            throw new NGCCAPlatformException("Write value error. " + e.getCause().getMessage());
        }
    }

    private <T> T readT(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new NGCCAPlatformException("Read value error. " + e.getCause().getMessage());
        }
    }
}
