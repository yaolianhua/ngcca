package io.hotcloud.application.api.template;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplateService {

    /**
     * Save or update instance template
     * @param instance instance template
     * @return saved or updated template object
     */
    InstanceTemplate saveOrUpdate(InstanceTemplate instance);

    /**
     * Find instance template with the giving id
     *
     * @param id instance template id
     * @return instance template
     */
    InstanceTemplate findOne(String id);

    /**
     * Find all instance template
     *
     * @return instance template collection
     */
    List<InstanceTemplate> findAll();

    /**
     * Find all user's insatnce template
     *
     * @param user user's username
     * @return instance template collection
     */
    List<InstanceTemplate> findAll(String user);

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
