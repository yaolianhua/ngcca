package io.hotcloud.buildpack.api.core;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackRunnerProcessor {

    /**
     * After the application is started, the initialization action of the buildpack relation
     */
    void process();
}
