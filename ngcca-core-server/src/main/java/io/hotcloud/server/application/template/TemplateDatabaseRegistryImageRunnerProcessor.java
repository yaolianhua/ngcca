package io.hotcloud.server.application.template;

import io.hotcloud.common.model.utils.Log;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.db.core.registry.RegistryImageEntity;
import io.hotcloud.module.db.core.registry.RegistryImageRepository;
import io.hotcloud.server.NGCCARunnerProcessor;
import io.hotcloud.server.registry.DatabaseRegistryImages;
import io.hotcloud.server.registry.RegistryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TemplateDatabaseRegistryImageRunnerProcessor implements NGCCARunnerProcessor {

    private final RegistryImageRepository registryImageRepository;
    private final RegistryProperties registryProperties;
    private final TemplateImagesProperties templateImagesProperties;
    private final DatabaseRegistryImages registryImagesContainer;

    @Override
    public void execute() {
        List<String> prints = new ArrayList<>();
        for (Template image : Template.values()) {
            String key = image.name().toLowerCase();
            String repo = templateImagesProperties.getRepos().get(key);
            String tag = templateImagesProperties.getTag(key);
            Assert.hasText(repo, String.format("%s image name is null", key));

            RegistryImageEntity entity = registryImageRepository.findByName(key);
            if (Objects.isNull(entity)) {
                RegistryImageEntity saveEntity = new RegistryImageEntity();
                saveEntity.setName(key);
                saveEntity.setTag(tag);
                saveEntity.setValue(String.format("%s/%s", registryProperties.getUrl(), repo));
                registryImageRepository.save(saveEntity);

                registryImagesContainer.put(key, saveEntity.getValue());
            } else {
                entity.setValue(String.format("%s/%s", registryProperties.getUrl(), repo));
                entity.setTag(tag);
                entity.setModifiedAt(LocalDateTime.now());
                registryImageRepository.save(entity);

                registryImagesContainer.put(key, entity.getValue());
            }

            prints.add(repo);
        }

        Log.info(TemplateDatabaseRegistryImageRunnerProcessor.class.getName(), String.format("Template images init success. items '%s'", prints));
    }
}
