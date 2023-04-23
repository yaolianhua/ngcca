package io.hotcloud.module.application.core;

public interface ApplicationDeploymentCacheApi {

    boolean tryLock(String id);

    void unLock(String id);

    Integer getTimeoutSeconds();
}
