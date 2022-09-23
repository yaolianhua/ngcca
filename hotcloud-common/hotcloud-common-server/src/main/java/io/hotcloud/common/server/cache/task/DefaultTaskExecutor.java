package io.hotcloud.common.server.cache.task;

import io.hotcloud.common.api.cache.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
public class DefaultTaskExecutor {

    private final List<BaseTask> tasks;

    public DefaultTaskExecutor(List<BaseTask> tasks) {
        this.tasks = tasks;
    }

    @PostConstruct
    public void execute (){
        for (BaseTask task : tasks) {
            task.run();
        }
    }
}
