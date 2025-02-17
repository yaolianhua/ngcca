package io.hotcloud.service.runner;

import io.hotcloud.db.entity.RegistryImageEntity;
import io.hotcloud.db.entity.RegistryImageRepository;
import io.hotcloud.service.registry.SystemRegistryImageProperties;
import io.hotcloud.service.registry.SystemRegistryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlatformSystemRegistryImageInitializeProcessor implements RunnerProcessor {

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
                continue;
            }

            // update info
            entity.setTag(systemRegistryImageProperties.getTag(name));
            entity.setValue(systemRegistryProperties.getUrl() + "/" + propertyPair.get(name));
            entity.setModifiedAt(Instant.now());
            registryImageRepository.save(entity);

        }
    }

}
