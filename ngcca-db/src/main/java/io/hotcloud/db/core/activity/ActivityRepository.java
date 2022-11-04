package io.hotcloud.db.core.activity;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ActivityRepository extends PagingAndSortingRepository<ActivityEntity, String> {

    /**
     * Find entity with the giving {@code user}
     *
     * @param user user's username
     * @return activity entities collection
     */
    List<ActivityEntity> findByUser(String user);
}
