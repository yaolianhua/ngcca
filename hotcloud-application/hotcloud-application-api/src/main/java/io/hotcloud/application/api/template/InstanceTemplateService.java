package io.hotcloud.application.api.template;

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
     * @param id instance template id
     * @return instance template
     */
    InstanceTemplate findOne(String id);

    /**
     * Delete instance template with the giving id
     * @param id instance template id
     */
    void delete(String id);
}
