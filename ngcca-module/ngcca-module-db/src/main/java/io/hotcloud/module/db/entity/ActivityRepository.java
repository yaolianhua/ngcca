package io.hotcloud.module.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ActivityRepository extends PagingAndSortingRepository<ActivityEntity, String>, CrudRepository<ActivityEntity, String> {

    /**
     * Find entity with the giving {@code user}
     *
     * @param user user's username
     * @return activity entities collection
     */
    List<ActivityEntity> findByUser(String user);
}
