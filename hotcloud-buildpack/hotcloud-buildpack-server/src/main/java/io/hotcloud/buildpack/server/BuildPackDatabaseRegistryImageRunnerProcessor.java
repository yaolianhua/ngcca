package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.api.core.BuildPackImages;
import io.hotcloud.buildpack.server.core.BuildPackImagesProperties;
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
public class BuildPackDatabaseRegistryImageRunnerProcessor implements CommonRunnerProcessor {

    private final RegistryImageRepository registryImageRepository;
    private final RegistryProperties registryProperties;
    private final BuildPackImagesProperties buildPackImagesProperties;

    public BuildPackDatabaseRegistryImageRunnerProcessor(RegistryImageRepository registryImageRepository,
                                                         RegistryProperties registryProperties,
                                                         BuildPackImagesProperties buildPackImagesProperties) {
        this.registryImageRepository = registryImageRepository;
        this.registryProperties = registryProperties;
        this.buildPackImagesProperties = buildPackImagesProperties;
    }

    @Override
    public void execute() {
        for (BuildPackImages image : BuildPackImages.values()) {
            String key = image.name().toLowerCase();
            String repo = buildPackImagesProperties.getRepos().get(key);
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

        String images = Arrays.stream(BuildPackImages.values()).map(Enum::name).collect(Collectors.joining(","));
        Log.info(BuildPackDatabaseRegistryImageRunnerProcessor.class.getName(), String.format("BuildPack images init success. items [%s]", images));
    }
}
