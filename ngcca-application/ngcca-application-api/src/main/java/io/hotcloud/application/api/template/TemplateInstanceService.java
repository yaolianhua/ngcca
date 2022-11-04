package io.hotcloud.application.api.template;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateInstanceService {

    /**
     * Save or update instance template
     * @param instance instance template
     * @return saved or updated template object
     */
    TemplateInstance saveOrUpdate(TemplateInstance instance);

    /**
     * Find instance template with the giving id
     *
     * @param id instance template id
     * @return instance template
     */
    TemplateInstance findOne(String id);

    /**
     * Find template with the giving {@code uuid}
     *
     * @param uuid business id
     * @return {@link TemplateInstance}
     */
    TemplateInstance findByUuid(String uuid);

    /**
     * Find all instance template
     *
     * @return instance template collection
     */
    List<TemplateInstance> findAll();

    /**
     * Find all user's insatnce template
     *
     * @param user user's username
     * @return instance template collection
     */
    List<TemplateInstance> findAll(String user);

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
