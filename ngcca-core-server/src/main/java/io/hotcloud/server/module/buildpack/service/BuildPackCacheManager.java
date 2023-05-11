package io.hotcloud.server.module.buildpack.service;

import io.hotcloud.module.buildpack.BuildPackCacheApi;
import io.hotcloud.module.buildpack.model.JobState;
import io.hotcloud.server.cache.Cache;
import org.springframework.stereotype.Component;


@Component
public class BuildPackCacheManager implements BuildPackCacheApi {

    private static final String CK_BUILD_STATE = "BuildPack:State:%s";
    private final Cache cache;

    public BuildPackCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void cacheBuildPackState(String buildPackId, JobState state) {
        cache.put(String.format(CK_BUILD_STATE, buildPackId), state);
    }

    @Override
    public JobState getBuildPackState(String buildPackId) {
        return cache.get(String.format(CK_BUILD_STATE, buildPackId), JobState.class);
    }
}
