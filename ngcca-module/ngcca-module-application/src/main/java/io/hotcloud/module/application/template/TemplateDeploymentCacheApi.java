package io.hotcloud.module.application.template;

public interface TemplateDeploymentCacheApi {

    boolean tryLock(String id);

    void unLock(String id);

    Integer getTimeoutSeconds();
}
