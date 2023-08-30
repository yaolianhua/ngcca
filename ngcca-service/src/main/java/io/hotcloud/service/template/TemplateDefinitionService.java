package io.hotcloud.service.template;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateDefinitionService {

    /**
     * Save or update template definition.
     * <p> id can not be null when update the template definition
     */
    TemplateDefinition saveOrUpdate(TemplateDefinition definition);

    /**
     * Find template definition with the giving id
     */
    TemplateDefinition findById(String id);

    /**
     * Find template definition with the giving name
     */
    TemplateDefinition findByName(String name);

    /**
     * Find template definition with the giving name
     */
    TemplateDefinition findByNameIgnoreCase(String name);

    /**
     * Find all template definition
     */
    List<TemplateDefinition> findAll();

    /**
     * Fuzzy Query all template definition with the giving name
     */
    List<TemplateDefinition> findAll(String name);

    /**
     * Delete template definition
     */
    void deleteById(String id);
}
