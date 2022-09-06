package io.hotcloud.db.core.application;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationInstanceRepository extends PagingAndSortingRepository<ApplicationInstanceEntity, String> {

    /**
     * Get Entity with the giving username and application name
     * @param name application name
     * @param user user's name
     * @return {@link ApplicationInstanceEntity}
     */
    ApplicationInstanceEntity findByNameAndUser (String name, String user);
}
