package io.hotcloud.module.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateDefinitionRepository extends PagingAndSortingRepository<TemplateDefinitionEntity, String>, CrudRepository<TemplateDefinitionEntity, String> {

    /**
     * Find entity with the giving template name
     *
     * @param name template name
     * @return {@link TemplateDefinitionEntity}
     */
    TemplateDefinitionEntity findByName(String name);

    /**
     * Find entities with the giving fuzzy template name
     */
    List<TemplateDefinitionEntity> findByNameLikeIgnoreCase(String name);
}
