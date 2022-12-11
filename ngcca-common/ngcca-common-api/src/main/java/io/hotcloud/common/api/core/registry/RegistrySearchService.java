package io.hotcloud.common.api.core.registry;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.registry.RegistryAuthentication;
import io.hotcloud.common.model.registry.RegistryRepository;
import io.hotcloud.common.model.registry.RegistryRepositoryTag;
import io.hotcloud.common.model.registry.RegistryType;

public interface RegistrySearchService {

    PageResult<RegistryRepository> listRepositories(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String query);

    PageResult<RegistryRepositoryTag> listTags(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String repository);
}
