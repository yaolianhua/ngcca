package io.hotcloud.buildpack.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackRunnerPostProcessor {

    /**
     * After the application is started, the initialization action of the buildpack relation
     */
    void execute();
}
