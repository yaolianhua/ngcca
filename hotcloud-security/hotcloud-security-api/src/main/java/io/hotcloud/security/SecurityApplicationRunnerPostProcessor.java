package io.hotcloud.security;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface SecurityApplicationRunnerPostProcessor {

    /**
     * After the application is started, the initialization action of the security relation
     */
    void execute();
}
