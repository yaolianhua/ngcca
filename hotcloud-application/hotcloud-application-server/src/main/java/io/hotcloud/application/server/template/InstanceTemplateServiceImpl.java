package io.hotcloud.application.server.template;

import io.hotcloud.application.api.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.db.core.application.InstanceTemplateEntity;
import io.hotcloud.db.core.application.InstanceTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class InstanceTemplateServiceImpl implements InstanceTemplateService {

    private final InstanceTemplateRepository instanceTemplateRepository;

    public InstanceTemplateServiceImpl(InstanceTemplateRepository instanceTemplateRepository) {
        this.instanceTemplateRepository = instanceTemplateRepository;
    }

    @Override
    public InstanceTemplate saveOrUpdate(InstanceTemplate instance) {
        Assert.notNull(instance, "Application instance body is null");

        if (StringUtils.hasText(instance.getId())) {
            InstanceTemplateEntity find = instanceTemplateRepository.findById(instance.getId()).orElseThrow(() -> new NullPointerException("instance template not found [" + instance.getId() + "]"));

            find.setModifiedAt(LocalDateTime.now());
            find.setSuccess(instance.isSuccess());
            find.setMessage(instance.getMessage());
            InstanceTemplateEntity updated = instanceTemplateRepository.save(find);

            return updated.toT(InstanceTemplate.class);
        }

        InstanceTemplateEntity existEntity = instanceTemplateRepository.findByUserAndName(instance.getUser(), instance.getName());
        if (existEntity != null) {
            throw new IllegalStateException("instance template [" + existEntity.getId() + "] already exist for user [" + existEntity.getUser() + "]");
        }

        InstanceTemplateEntity entity = (InstanceTemplateEntity) new InstanceTemplateEntity().copyToEntity(instance);
        entity.setCreatedAt(LocalDateTime.now());

        InstanceTemplateEntity saved = instanceTemplateRepository.save(entity);

        return saved.toT(InstanceTemplate.class);
    }

    @Override
    public InstanceTemplate findOne(String id) {
        InstanceTemplateEntity entity = instanceTemplateRepository.findById(id).orElse(null);
        return entity == null ? null : entity.toT(InstanceTemplate.class);
    }

    @Override
    public void delete(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        instanceTemplateRepository.deleteById(id);
    }
}
