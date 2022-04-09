package io.hotcloud.db.core.user;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {

    /**
     * Find UserEntity with the giving {@code username}
     *
     * @param username username
     * @return {@link  UserEntity}
     */
    UserEntity findByUsername(String username);

    /**
     * Delete user with giving {@code username} physically
     *
     * @param username username
     * @return true/false
     */
    boolean deleteByUsername(String username);
}
