package io.hotcloud.module.buildpack;

import io.hotcloud.module.buildpack.model.JobState;

public interface BuildPackCacheApi {

    void cacheBuildPackState(String buildPackId, JobState state);

    JobState getBuildPackState(String buildPackId);
}
