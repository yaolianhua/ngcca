package io.hotcloud.module.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface GitClonedRepository extends PagingAndSortingRepository<GitClonedEntity, String>, CrudRepository<GitClonedEntity, String> {

    /**
     * Find GitCloneEntity with the giving {@code user} and {@code project}
     *
     * @param user    user's username
     * @param project git project name
     * @return {@link GitClonedEntity}
     */
    GitClonedEntity findByUserAndProject(String user, String project);

    /**
     * List entity with the giving {@code  user}
     *
     * @param user user
     * @return {@link GitClonedEntity}
     */
    List<GitClonedEntity> findByUser(String user);

}
