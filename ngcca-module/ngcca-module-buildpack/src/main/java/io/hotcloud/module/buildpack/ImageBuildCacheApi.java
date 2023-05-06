package io.hotcloud.module.buildpack;

import io.hotcloud.module.buildpack.model.JobState;

public interface ImageBuildCacheApi {

    void setStatus(String buildPackId, JobState status);

    JobState getStatus(String buildPackId);

    boolean tryLock(String buildPackId);

    void unLock(String buildPackId);

    Integer getTimeoutSeconds();
}
