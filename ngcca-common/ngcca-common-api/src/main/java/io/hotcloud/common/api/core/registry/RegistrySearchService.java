package io.hotcloud.common.api.core.registry;

import io.hotcloud.common.api.core.registry.model.RegistryAuthentication;
import io.hotcloud.common.api.core.registry.model.RegistryRepository;
import io.hotcloud.common.api.core.registry.model.RegistryRepositoryTag;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;

public interface RegistrySearchService {

    PageResult<RegistryRepository> listRepositories(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String query);

    PageResult<RegistryRepositoryTag> listTags(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String repository);
}
