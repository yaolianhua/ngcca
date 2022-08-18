package io.hotcloud.application.api.template;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplatePlayer {

    /**
     * Deploy instance template
     *
     * @param template {@link Template}
     * @return {@link InstanceTemplate}
     */
    InstanceTemplate play(Template template);

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
