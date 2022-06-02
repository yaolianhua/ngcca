package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateDefinition;
import io.hotcloud.application.api.template.TemplateDefinitionService;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.exception.HotCloudResourceNotFoundException;
import io.hotcloud.db.core.application.TemplateDefinitionEntity;
import io.hotcloud.db.core.application.TemplateDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class TemplateDefinitionServiceImpl implements TemplateDefinitionService {

    private final TemplateDefinitionRepository templateDefinitionRepository;

    public TemplateDefinitionServiceImpl(TemplateDefinitionRepository templateDefinitionRepository) {
        this.templateDefinitionRepository = templateDefinitionRepository;
    }

    private void templateDefinitionNameVerified(String name) {
        if (!StringUtils.hasText(name)) {
            return;
        }
        List<String> names = Arrays.stream(Template.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        if (!names.contains(name)) {
            throw new HotCloudException("Supported template " + names);
        }
    }

    @Override
    public TemplateDefinition saveOrUpdate(TemplateDefinition definition) {

        Assert.notNull(definition, "TemplateDefinition body is null");

        if (StringUtils.hasText(definition.getId())) {
            TemplateDefinitionEntity existed = templateDefinitionRepository.findById(definition.getId()).orElseThrow(() -> new HotCloudResourceNotFoundException("Template definition not found [" + definition.getId() + "]"));
            if (StringUtils.hasText(definition.getName())) {
                templateDefinitionNameVerified(definition.getName());
                existed.setName(definition.getName());
            }
            if (StringUtils.hasText(definition.getVersion())) {
                existed.setVersion(definition.getVersion());
            }
            if (StringUtils.hasText(definition.getDescription())) {
                existed.setDescription(definition.getDescription());
            }
            if (StringUtils.hasText(definition.getShortDesc())) {
                existed.setShortDesc(definition.getShortDesc());
            }
            if (StringUtils.hasText(definition.getLogo())) {
                existed.setLogo(definition.getLogo());
            }

            existed.setModifiedAt(LocalDateTime.now());

            TemplateDefinitionEntity updated = templateDefinitionRepository.save(existed);
            return updated.toT(TemplateDefinition.class);
        }


        Assert.hasText(definition.getName(), "Template definition name is null");
        templateDefinitionNameVerified(definition.getName());

        TemplateDefinitionEntity saveEntity = (TemplateDefinitionEntity) new TemplateDefinitionEntity().copyToEntity(definition);
        saveEntity.setCreatedAt(LocalDateTime.now());
        TemplateDefinitionEntity saved = templateDefinitionRepository.save(saveEntity);

        return saved.toT(TemplateDefinition.class);
    }

    @Override
    public TemplateDefinition findById(String id) {
        TemplateDefinitionEntity existed = templateDefinitionRepository.findById(id).orElseThrow(() -> new HotCloudResourceNotFoundException("Template definition not found [" + id + "]"));
        return existed.toT(TemplateDefinition.class);
    }

    @Override
    public TemplateDefinition findByName(String name) {
        TemplateDefinitionEntity entity = templateDefinitionRepository.findByName(name);
        if (null == entity) {
            throw new HotCloudResourceNotFoundException("Template definition not found [" + name + "]");
        }
        return entity.toT(TemplateDefinition.class);
    }

    @Override
    public List<TemplateDefinition> findAll() {
        Iterable<TemplateDefinitionEntity> iterable = templateDefinitionRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(e -> e.toT(TemplateDefinition.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateDefinition> findAll(String name) {

        if (!StringUtils.hasText(name)) {
            return findAll();
        }

        Iterable<TemplateDefinitionEntity> iterable = templateDefinitionRepository.findByNameLikeIgnoreCase(name);
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(e -> e.toT(TemplateDefinition.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        TemplateDefinitionEntity existed = templateDefinitionRepository.findById(id).orElseThrow(() -> new HotCloudResourceNotFoundException("Template definition not found [" + id + "]"));
        templateDefinitionRepository.delete(existed);
    }
}
