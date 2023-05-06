package io.hotcloud.server;

import io.hotcloud.module.db.core.registry.RegistryImageEntity;
import io.hotcloud.module.db.core.registry.RegistryImageRepository;
import io.hotcloud.server.registry.SystemRegistryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SystemRegistryImageRunnerProcessor implements NGCCARunnerProcessor {

    private final RegistryImageRepository registryImageRepository;
    private final SystemRegistryProperties systemRegistryProperties;
    private final SystemRegistryImageProperties systemRegistryImageProperties;

    @Override
    public void execute() {

        Map<String, String> propertyPair = systemRegistryImageProperties.getPropertyPair();
        for (String name : propertyPair.keySet()) {
            RegistryImageEntity entity = registryImageRepository.findByName(name);
            if (entity == null) {
                RegistryImageEntity saved = new RegistryImageEntity();

                saved.setName(name);
                saved.setTag(systemRegistryImageProperties.getTag(name));
                saved.setValue(systemRegistryProperties.getUrl() + "/" + propertyPair.get(name));
                registryImageRepository.save(saved);
                return;
            }

            // update info
            entity.setTag(systemRegistryImageProperties.getTag(name));
            entity.setValue(systemRegistryProperties.getUrl() + "/" + propertyPair.get(name));
            entity.setModifiedAt(LocalDateTime.now());
            registryImageRepository.save(entity);

        }
    }

}
