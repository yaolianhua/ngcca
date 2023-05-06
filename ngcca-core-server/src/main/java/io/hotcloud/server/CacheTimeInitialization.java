package io.hotcloud.server;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.server.cache.Cache;
import org.springframework.stereotype.Component;

@Component
public class CacheTimeInitialization implements NGCCARunnerProcessor {

    private final Cache cache;

    public CacheTimeInitialization(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void execute() {
        cache.put(CommonConstant.CK_DEPLOYMENT_TIMEOUT_SECONDS, 1200);
        Log.info(this, null, Event.START, "Cached deployment-timeout-seconds. value='1200s'");
    }
}
