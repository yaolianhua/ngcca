package io.hotcloud.common.api.cache.task;

public abstract class BaseTask implements Runnable{

    protected abstract void onSuccess (TaskContext context, TaskResponse response);

    protected abstract void onFailed (TaskContext context, TaskResponse response);

    protected abstract void onIgnore (TaskContext context, TaskResponse response);

    protected abstract int order ();
}
