package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.module.buildpack.ImageBuildCacheApi;
import io.hotcloud.module.buildpack.JobState;
import io.hotcloud.server.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.*;

@Component
public class ImageBuildCacheManager implements ImageBuildCacheApi {

    private final Cache cache;

    public ImageBuildCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void setStatus(String buildPackId, JobState status) {
        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPackId), status);
    }

    @Override
    public JobState getStatus(String buildPackId) {
        return cache.get(String.format(CommonConstant.CK_IMAGEBUILD_STATUS, buildPackId), JobState.class);
    }

    @Override
    public boolean tryLock(String buildPackId) {
        Object o = cache.get(String.format(CK_IMAGEBUILD_WATCHED, buildPackId));
        if (Objects.nonNull(o)) {
            return false;
        }
        cache.put(String.format(CK_IMAGEBUILD_WATCHED, buildPackId), Boolean.TRUE);
        return true;
    }

    @Override
    public void unLock(String buildPackId) {
        cache.evict(String.format(CK_IMAGEBUILD_WATCHED, buildPackId));
    }

    @Override
    public Integer getTimeoutSeconds() {
        return cache.get(CK_IMAGEBUILD_TIMEOUT_SECONDS, Integer.class);
    }
}
