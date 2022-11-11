package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.model.exception.NGCCAResourceNotFoundException;
import io.hotcloud.db.core.application.TemplateInstanceEntity;
import io.hotcloud.db.core.application.TemplateInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class TemplateInstanceServiceImpl implements TemplateInstanceService {

    private final TemplateInstanceRepository templateInstanceRepository;

    public TemplateInstanceServiceImpl(TemplateInstanceRepository templateInstanceRepository) {
        this.templateInstanceRepository = templateInstanceRepository;
    }

    @Override
    public TemplateInstance saveOrUpdate(TemplateInstance instance) {
        Assert.notNull(instance, "Application instance body is null");

        if (StringUtils.hasText(instance.getId())) {
            TemplateInstanceEntity find = templateInstanceRepository.findById(instance.getId()).orElseThrow(() -> new NGCCAResourceNotFoundException("instance template not found [" + instance.getId() + "]"));

            find.setModifiedAt(LocalDateTime.now());
            find.setSuccess(instance.isSuccess());
            find.setMessage(instance.getMessage());
            find.setNodePorts(instance.getNodePorts());
            TemplateInstanceEntity updated = templateInstanceRepository.save(find);

            return updated.toT(TemplateInstance.class);
        }

        TemplateInstanceEntity existEntity = templateInstanceRepository.findByUserAndName(instance.getUser(), instance.getName());
        if (existEntity != null) {
            throw new IllegalStateException("instance template [" + existEntity.getId() + "] already exist for user [" + existEntity.getUser() + "]");
        }

        TemplateInstanceEntity entity = (TemplateInstanceEntity) new TemplateInstanceEntity().copyToEntity(instance);
        entity.setCreatedAt(LocalDateTime.now());

        TemplateInstanceEntity saved = templateInstanceRepository.save(entity);

        return saved.toT(TemplateInstance.class);
    }

    @Override
    public TemplateInstance findOne(String id) {
        TemplateInstanceEntity entity = templateInstanceRepository.findById(id).orElse(null);
        return entity == null ? null : entity.toT(TemplateInstance.class);
    }

    @Override
    public TemplateInstance findByUuid(String uuid) {
        TemplateInstanceEntity entity = templateInstanceRepository.findByUuid(uuid);
        return entity == null ? null : entity.toT(TemplateInstance.class);
    }

    @Override
    public List<TemplateInstance> findAll() {
        Iterable<TemplateInstanceEntity> entityIterable = templateInstanceRepository.findAll();
        return StreamSupport.stream(entityIterable.spliterator(), false)
                .map(e -> e.toT(TemplateInstance.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateInstance> findAll(String user) {
        return templateInstanceRepository.findByUser(user)
                .stream()
                .map(e -> e.toT(TemplateInstance.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        templateInstanceRepository.deleteById(id);
    }
}
