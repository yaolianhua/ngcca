package io.hotcloud.db.core.application;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ApplicationInstanceRepository extends PagingAndSortingRepository<ApplicationInstanceEntity, String> {

    /**
     * Get Entities with the giving username and application name
     * @param name application name
     * @param user user's name
     * @return {@link ApplicationInstanceEntity}
     */
    List<ApplicationInstanceEntity> findByNameAndUser (String name, String user);
}
