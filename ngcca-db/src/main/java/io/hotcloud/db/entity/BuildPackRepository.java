package io.hotcloud.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackRepository extends PagingAndSortingRepository<BuildPackEntity, String>, CrudRepository<BuildPackEntity, String> {


    /**
     * Find entity with giving {@code user}
     *
     * @param user user's username
     * @return {@link BuildPackEntity}
     */
    List<BuildPackEntity> findByUser(String user);


    /**
     * Find entity with giving {@code uuid}
     *
     * @param uuid business id
     * @return {@link BuildPackEntity}
     */
    BuildPackEntity findByUuid(String uuid);
}
