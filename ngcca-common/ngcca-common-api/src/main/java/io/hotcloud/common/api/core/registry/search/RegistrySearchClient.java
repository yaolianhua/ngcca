package io.hotcloud.common.api.core.registry.search;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.registry.RegistryAuthentication;
import io.hotcloud.common.model.registry.RegistryRepository;
import io.hotcloud.common.model.registry.RegistryRepositoryTag;

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
