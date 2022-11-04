package io.hotcloud.application.api.template;

public interface TemplateDeploymentCacheApi {

    boolean tryLock (String id);

    void unLock (String id);

    Integer getTimeoutSeconds ();
}
