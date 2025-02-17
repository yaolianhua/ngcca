package io.hotcloud.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ApplicationInstanceRepository extends PagingAndSortingRepository<ApplicationInstanceEntity, String>, CrudRepository<ApplicationInstanceEntity, String> {

    /**
     * Get Entities with the giving username and application name
     *
     * @param name application name
     * @param user user's name
     * @return {@link ApplicationInstanceEntity}
     */
    List<ApplicationInstanceEntity> findByNameAndUser(String name, String user);

    /**
     * Get Entities with the giving username
     *
     * @param user user's name
     * @return {@link ApplicationInstanceEntity}
     */
    List<ApplicationInstanceEntity> findByUser(String user);
}
