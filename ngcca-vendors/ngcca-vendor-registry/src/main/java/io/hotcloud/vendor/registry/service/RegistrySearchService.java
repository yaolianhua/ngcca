package io.hotcloud.vendor.registry.service;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.RegistryType;

public interface RegistrySearchService {

    PageResult<RegistryRepository> listRepositories(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String query);

    PageResult<RegistryRepositoryTag> listTags(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String repository);
}
