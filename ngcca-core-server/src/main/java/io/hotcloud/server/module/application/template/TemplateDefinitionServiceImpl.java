package io.hotcloud.server.module.application.template;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.common.model.exception.ResourceConflictException;
import io.hotcloud.common.model.exception.ResourceNotFoundException;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateDefinition;
import io.hotcloud.module.application.template.TemplateDefinitionService;
import io.hotcloud.module.db.core.application.TemplateDefinitionEntity;
import io.hotcloud.module.db.core.application.TemplateDefinitionRepository;
import io.hotcloud.server.module.registry.SystemRegistryImageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class TemplateDefinitionServiceImpl implements TemplateDefinitionService {

    private final TemplateDefinitionRepository templateDefinitionRepository;
    private final SystemRegistryImageProperties systemRegistryImageProperties;

    public TemplateDefinitionServiceImpl(TemplateDefinitionRepository templateDefinitionRepository,
                                         SystemRegistryImageProperties systemRegistryImageProperties) {
        this.templateDefinitionRepository = templateDefinitionRepository;
        this.systemRegistryImageProperties = systemRegistryImageProperties;
    }

    private void validateTemplateDefinitionName(String name) {
        if (!StringUtils.hasText(name)) {
            return;
        }
        List<String> names = Arrays.stream(Template.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        if (!names.contains(name)) {
            throw new PlatformException("Supported template " + names);
        }
    }

    private void validateTemplateDefinitionVersion(String name, String version) {
        String tag = systemRegistryImageProperties.getTag(name);
        if (!Objects.equals(tag, version)) {
            throw new PlatformException("Template [" + name + "] supported version [" + tag + "]", 400);
        }
    }

    @Override
    public TemplateDefinition saveOrUpdate(TemplateDefinition definition) {

        Assert.notNull(definition, "TemplateDefinition body is null");

        if (StringUtils.hasText(definition.getId())) {
            TemplateDefinitionEntity existed = templateDefinitionRepository.findById(definition.getId()).orElseThrow(() -> new ResourceNotFoundException("Template definition not found [" + definition.getId() + "]"));
            if (StringUtils.hasText(definition.getName())) {
                validateTemplateDefinitionName(definition.getName());
                existed.setName(definition.getName());
            }
            if (StringUtils.hasText(definition.getVersion())) {
                validateTemplateDefinitionVersion(definition.getName(), definition.getVersion());
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
        validateTemplateDefinitionName(definition.getName());
        validateTemplateDefinitionVersion(definition.getName(), definition.getVersion());

        TemplateDefinitionEntity entity = templateDefinitionRepository.findByName(definition.getName());
        if (Objects.nonNull(entity)) {
            throw new ResourceConflictException("Template definition already exist [" + definition.getName() + "]");
        }

        TemplateDefinitionEntity saveEntity = (TemplateDefinitionEntity) new TemplateDefinitionEntity().toE(definition);
        saveEntity.setCreatedAt(LocalDateTime.now());
        saveEntity.setModifiedAt(LocalDateTime.now());
        TemplateDefinitionEntity saved = templateDefinitionRepository.save(saveEntity);

        return saved.toT(TemplateDefinition.class);
    }

    @Override
    public TemplateDefinition findById(String id) {
        TemplateDefinitionEntity existed = templateDefinitionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Template definition not found [" + id + "]"));
        return existed.toT(TemplateDefinition.class);
    }

    @Override
    public TemplateDefinition findByName(String name) {
        TemplateDefinitionEntity entity = templateDefinitionRepository.findByName(name);
        if (null == entity) {
            throw new ResourceNotFoundException("Template definition not found [" + name + "]");
        }
        return entity.toT(TemplateDefinition.class);
    }

    @Override
    public TemplateDefinition findByNameIgnoreCase(String name) {
        if (!StringUtils.hasText(name)) {
            throw new PlatformException("parameter name is null");
        }
        List<TemplateDefinition> definitions = this.findAll();
        for (TemplateDefinition definition : definitions) {
            if (Objects.equals(definition.getName().toLowerCase(), name.toLowerCase())) {
                return definition;
            }
        }
        throw new ResourceNotFoundException("Template definition not found [" + name + "]");
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
        TemplateDefinitionEntity existed = templateDefinitionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Template definition not found [" + id + "]"));
        templateDefinitionRepository.delete(existed);
    }
}
