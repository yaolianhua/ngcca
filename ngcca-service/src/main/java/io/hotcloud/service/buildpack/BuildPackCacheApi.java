package io.hotcloud.service.buildpack;

import io.hotcloud.service.buildpack.model.JobState;

public interface BuildPackCacheApi {

    void cacheBuildPackState(String buildPackId, JobState state);

    JobState getBuildPackState(String buildPackId);
}
