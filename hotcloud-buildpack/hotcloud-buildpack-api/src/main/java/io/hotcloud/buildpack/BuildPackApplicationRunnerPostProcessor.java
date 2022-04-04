package io.hotcloud.buildpack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApplicationRunnerPostProcessor {

    /**
     * After the application is started, the initialization action of the buildpack relation
     */
    void execute();
}
