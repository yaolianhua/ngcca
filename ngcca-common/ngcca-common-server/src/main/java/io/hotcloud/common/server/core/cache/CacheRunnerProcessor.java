package io.hotcloud.common.server.core.cache;

import io.hotcloud.common.api.NGCCARunnerProcessor;
import io.hotcloud.common.api.core.cache.Cache;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.utils.Log;
import org.springframework.stereotype.Component;

@Component
public class CacheRunnerProcessor implements NGCCARunnerProcessor {

    private final Cache cache;

    public CacheRunnerProcessor(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void execute() {
        cache.put(CommonConstant.CK_IMAGEBUILD_TIMEOUT_SECONDS, 1200);
        Log.info(CacheRunnerProcessor.class.getName(), "Cached imagebuild-timeout-seconds. value='1200s'");
        cache.put(CommonConstant.CK_DEPLOYMENT_TIMEOUT_SECONDS, 600);
        Log.info(CacheRunnerProcessor.class.getName(), "Cached deployment-timeout-seconds. value='600s'");
    }
}
