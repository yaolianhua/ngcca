package io.hotcloud.db.core.buildpack;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackRepository extends PagingAndSortingRepository<BuildPackEntity, String> , CrudRepository<BuildPackEntity, String> {

    /**
     * Find all with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPackEntity}
     */
    List<BuildPackEntity> findByUserAndClonedId(String user, String clonedId);

    /**
     * Find entity with giving {@code user}
     *
     * @param user user's username
     * @return {@link BuildPackEntity}
     */
    List<BuildPackEntity> findByUser(String user);

    /**
     * Find entity with giving {@code clonedId}
     *
     * @param clonedId git cloned id
     * @return {@link BuildPackEntity}
     */
    List<BuildPackEntity> findByClonedId(String clonedId);

    /**
     * Find entity with giving {@code uuid}
     * @param uuid business id
     * @return {@link BuildPackEntity}
     */
    BuildPackEntity findByUuid(String uuid);
}
