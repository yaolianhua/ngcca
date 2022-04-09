package io.hotcloud.db.core.buildpack;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface GitClonedRepository extends PagingAndSortingRepository<GitClonedEntity, String> {

    /**
     * Find GitCloneEntity with the giving {@code user} and {@code project}
     *
     * @param user    user's username
     * @param project git project name
     * @return {@link GitClonedEntity}
     */
    GitClonedEntity findByUserAndProject(String user, String project);

}
