package io.hotcloud.application.api.core;

public interface ApplicationDeploymentCacheApi {

    boolean tryLock (String id);

    void unLock (String id);

    Integer getTimeoutSeconds ();
}
