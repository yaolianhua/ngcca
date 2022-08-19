package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.registry.RegistryProperties;
import io.hotcloud.db.core.registry.RegistryImageEntity;
import io.hotcloud.db.core.registry.RegistryImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TemplateDatabaseRegistryImageRunnerProcessor implements CommonRunnerProcessor {

    private final RegistryImageRepository registryImageRepository;
    private final RegistryProperties registryProperties;
    private final TemplateImagesProperties templateImagesProperties;

    public TemplateDatabaseRegistryImageRunnerProcessor(RegistryImageRepository registryImageRepository,
                                                        RegistryProperties registryProperties,
                                                        TemplateImagesProperties templateImagesProperties) {
        this.registryImageRepository = registryImageRepository;
        this.registryProperties = registryProperties;
        this.templateImagesProperties = templateImagesProperties;
    }

    @Override
    public void execute() {
        for (Template image : Template.values()) {
            String key = image.name().toLowerCase();
            String repo = templateImagesProperties.getRepos().get(key);
            Assert.hasText(repo, String.format("%s image name is null", key));

            RegistryImageEntity entity = registryImageRepository.findByName(key);
            if (Objects.isNull(entity)) {
                RegistryImageEntity saveEntity = new RegistryImageEntity();
                saveEntity.setName(key);
                saveEntity.setValue(String.format("%s/%s", registryProperties.getUrl(), repo));
                registryImageRepository.save(saveEntity);
            }
            else {
                entity.setValue(String.format("%s/%s", registryProperties.getUrl(), repo));
                entity.setModifiedAt(LocalDateTime.now());
                registryImageRepository.save(entity);
            }
        }

        String images = Arrays.stream(Template.values()).map(Enum::name).collect(Collectors.joining(","));
        Log.info(TemplateDatabaseRegistryImageRunnerProcessor.class.getName(), String.format("Template images init success. items [%s]", images));
    }
}
