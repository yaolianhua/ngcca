package io.hotcloud.db.api.user;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    /**
     * Find UserEntity with the giving {@code username}
     *
     * @param username username
     * @return {@link  UserEntity}
     */
    UserEntity findByUsername(String username);
}
