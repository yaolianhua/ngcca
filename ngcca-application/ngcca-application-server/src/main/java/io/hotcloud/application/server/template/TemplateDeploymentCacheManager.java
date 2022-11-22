package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.TemplateDeploymentCacheApi;
import io.hotcloud.common.api.core.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.CK_DEPLOYMENT_TIMEOUT_SECONDS;
import static io.hotcloud.common.model.CommonConstant.CK_TEMPLATE_WATCHED;

@Component
public class TemplateDeploymentCacheManager implements TemplateDeploymentCacheApi {

    private final Cache cache;

    public TemplateDeploymentCacheManager(Cache cache) {
        this.cache = cache;
    }

    @Override
    public boolean tryLock(String id) {
        Object o = cache.get(String.format(CK_TEMPLATE_WATCHED, id));
        if (Objects.nonNull(o)){
            return false;
        }
        cache.put(String.format(CK_TEMPLATE_WATCHED, id), Boolean.TRUE);
        return true;
    }

    @Override
    public void unLock(String id) {
        cache.evict(String.format(CK_TEMPLATE_WATCHED, id));
    }

    @Override
    public Integer getTimeoutSeconds() {
        return cache.get(CK_DEPLOYMENT_TIMEOUT_SECONDS, Integer.class);
    }
}
