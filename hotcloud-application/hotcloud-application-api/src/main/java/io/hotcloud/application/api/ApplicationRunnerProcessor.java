package io.hotcloud.application.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ApplicationRunnerProcessor {

    /**
     * After the application is started, the initialization action of the application relation
     */
    void process();
}
