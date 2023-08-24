package io.hotcloud.service.application.template;

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
    TemplateInstance play(Template template);

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
