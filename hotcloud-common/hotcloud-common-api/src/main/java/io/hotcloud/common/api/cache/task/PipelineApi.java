package io.hotcloud.common.api.cache.task;

import java.util.List;

public interface PipelineApi {

    List<PipelineModel> list ();

    PipelineModel fetch (String id);

}
