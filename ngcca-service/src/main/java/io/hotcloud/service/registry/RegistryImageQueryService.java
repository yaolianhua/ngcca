package io.hotcloud.service.registry;

import io.hotcloud.db.entity.RegistryImageEntity;
import io.hotcloud.db.entity.RegistryImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistryImageQueryService {
    private final RegistryImageRepository registryImageRepository;

    public RegistryImageQueryService(RegistryImageRepository registryImageRepository) {
        this.registryImageRepository = registryImageRepository;
    }

    public Collection<RegistryImage> list() {
        Iterable<RegistryImageEntity> imageEntityIterable = registryImageRepository.findAll();
        return StreamSupport.stream(imageEntityIterable.spliterator(), false)
                .map(RegistryImage::build)
                .collect(Collectors.toList());
    }

    public RegistryImage query(String name) {
        RegistryImageEntity registryImage = registryImageRepository.findByName(name);
        return RegistryImage.build(registryImage);
    }
}
