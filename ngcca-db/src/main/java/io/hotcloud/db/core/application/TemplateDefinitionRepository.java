package io.hotcloud.db.core.application;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateDefinitionRepository extends PagingAndSortingRepository<TemplateDefinitionEntity, String> {

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
