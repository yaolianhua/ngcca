package io.hotcloud.common.server.cache.task;

import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.cache.task.PipelineApi;
import io.hotcloud.common.api.cache.task.PipelineModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PipelineService implements PipelineApi {

    private final Cache cache;

    public PipelineService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public List<PipelineModel> list() {
        return null;
    }

    @Override
    public PipelineModel fetch(String id) {
        return null;
    }
}
