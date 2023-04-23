package io.hotcloud.vendor.registry;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;

public interface RegistrySearchClient {

    /**
     * 查询repository
     *
     * @param authentication 认证信息
     * @param pageable       分页参数
     * @param query          查询关键字
     * @return RegistryRepository
     */
    PageResult<RegistryRepository> searchRepositories(RegistryAuthentication authentication, Pageable pageable, String query);

    /**
     * 查询仓库镜像tag
     *
     * @param authentication 认证信息
     * @param pageable       分页参数
     * @param repository     仓库镜像 e.g. library/nginx
     * @return RegistryRepositoryTag
     */
    PageResult<RegistryRepositoryTag> searchTags(RegistryAuthentication authentication, Pageable pageable, String repository);
}
