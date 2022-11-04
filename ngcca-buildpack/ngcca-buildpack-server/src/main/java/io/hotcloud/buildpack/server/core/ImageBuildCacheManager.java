package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.ImageBuildCacheApi;
import io.hotcloud.buildpack.api.core.ImageBuildStatus;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.hotcloud.common.api.CommonConstant.*;

@Component
public class ImageBuildCacheManager implements ImageBuildCacheApi {

    private final Cache cache;

    public ImageBuildCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void setStatus(String buildPackId, ImageBuildStatus status) {
        cache.put(String.format(CK_IMAGEBUILD_STATUS, buildPackId), status);
    }

    @Override
    public ImageBuildStatus getStatus(String buildPackId) {
        return cache.get(String.format(CommonConstant.CK_IMAGEBUILD_STATUS, buildPackId), ImageBuildStatus.class);
    }

    @Override
    public boolean tryLock(String buildPackId) {
        Object o = cache.get(String.format(CK_IMAGEBUILD_WATCHED, buildPackId));
        if (Objects.nonNull(o)){
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
