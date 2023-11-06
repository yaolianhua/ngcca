package io.hotcloud.service.template;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateInstancePlayer {

    /**
     * Deploy instance template
     *
     * @param template {@link Template}
     * @return {@link TemplateInstance}
     */
    TemplateInstance play(String clusterId, Template template);

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
