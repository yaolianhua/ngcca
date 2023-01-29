package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.server.core.BuildPackImagesProperties;
import io.hotcloud.common.api.NGCCARunnerProcessor;
import io.hotcloud.common.api.core.registry.DatabaseRegistryImages;
import io.hotcloud.common.autoconfigure.RegistryProperties;
import io.hotcloud.common.model.RuntimeImages;
import io.hotcloud.common.model.utils.Log;
import io.hotcloud.db.core.registry.RegistryImageEntity;
import io.hotcloud.db.core.registry.RegistryImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BuildPackDatabaseRegistryImageRunnerProcessor implements NGCCARunnerProcessor {

    private final RegistryImageRepository registryImageRepository;
    private final RegistryProperties registryProperties;
    private final BuildPackImagesProperties buildPackImagesProperties;
    private final DatabaseRegistryImages registryImagesContainer;

    @Override
    public void execute() {
        List<String> prints = new ArrayList<>();
        for (RuntimeImages image : RuntimeImages.values()) {
            String key = image.name().toLowerCase();
            String repo = buildPackImagesProperties.getRepos().get(key);
            String tag = buildPackImagesProperties.getTag(key);
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

        Log.info(BuildPackDatabaseRegistryImageRunnerProcessor.class.getName(), String.format("BuildPack images init success. items '%s'", prints));
    }
}
