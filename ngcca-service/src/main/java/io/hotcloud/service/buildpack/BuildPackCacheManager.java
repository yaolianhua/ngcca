package io.hotcloud.service.buildpack;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.service.buildpack.model.JobState;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class BuildPackCacheManager implements BuildPackCacheApi {

    private static final String CK_BUILD_STATE = "BuildPack:State:%s";
    private final Cache cache;

    public BuildPackCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void cacheBuildPackState(String buildPackId, JobState state) {
        cache.put(String.format(CK_BUILD_STATE, buildPackId), state, 3L, TimeUnit.HOURS);
    }

    @Override
    public void removeBuildPackState(String buildPackId) {
        cache.evict(String.format(CK_BUILD_STATE, buildPackId));
    }

    @Override
    public JobState getBuildPackState(String buildPackId) {
        return cache.get(String.format(CK_BUILD_STATE, buildPackId), JobState.class);
    }
}
