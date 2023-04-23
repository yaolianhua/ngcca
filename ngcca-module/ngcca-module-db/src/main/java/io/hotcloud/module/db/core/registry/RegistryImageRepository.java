package io.hotcloud.module.db.core.registry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegistryImageRepository extends PagingAndSortingRepository<RegistryImageEntity, String>, CrudRepository<RegistryImageEntity, String> {

    RegistryImageEntity findByName(String name);
}
