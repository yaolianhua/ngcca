package io.hotcloud.db.core.buildpack;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackRepository extends PagingAndSortingRepository<BuildPackEntity, String> {

    /**
     * Find all with giving {@code user} and {@code clonedId}
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPackEntity}
     */
    List<BuildPackEntity> findByUserAndClonedId(String user, String clonedId);
}
