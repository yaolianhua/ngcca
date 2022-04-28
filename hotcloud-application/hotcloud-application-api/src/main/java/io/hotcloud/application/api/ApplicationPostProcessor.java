package io.hotcloud.application.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ApplicationPostProcessor {

    /**
     * After the application is started, the initialization action of the application relation
     */
    void execute();
}
