package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationDeploymentCacheApi;
import io.hotcloud.common.api.core.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.hotcloud.common.api.CommonConstant.CK_APPLICATION_WATCHED;
import static io.hotcloud.common.api.CommonConstant.CK_DEPLOYMENT_TIMEOUT_SECONDS;

@Component
public class ApplicationDeploymentCacheManager implements ApplicationDeploymentCacheApi {

    private final Cache cache;

    public ApplicationDeploymentCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public boolean tryLock(String id) {
        Object o = cache.get(String.format(CK_APPLICATION_WATCHED, id));
        if (Objects.nonNull(o)){
            return false;
        }
        cache.put(String.format(CK_APPLICATION_WATCHED, id), Boolean.TRUE);
        return true;
    }

    @Override
    public void unLock(String id) {
        cache.evict(String.format(CK_APPLICATION_WATCHED, id));
    }

    @Override
    public Integer getTimeoutSeconds() {
        return cache.get(CK_DEPLOYMENT_TIMEOUT_SECONDS, Integer.class);
    }
}
