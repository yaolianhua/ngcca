package io.hotcloud.db.core.registry;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegistryImageRepository extends PagingAndSortingRepository<RegistryImageEntity, String> {

    RegistryImageEntity findByName(String name);
}
