package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.utils.Log;
import org.eclipse.jgit.lib.ProgressMonitor;

/**
 * @author yaolianhua789@gmail.com
 **/
public class SimpleProgressMonitor implements ProgressMonitor {

    @Override
    public void start(int totalTasks) {
        Log.debug(SimpleProgressMonitor.class.getName(),
                String.format("Starting work on '%s' tasks", totalTasks));
    }

    @Override
    public void beginTask(String title, int totalWork) {
        Log.debug(SimpleProgressMonitor.class.getName(),
                String.format("Start '%s': '%s'", title, totalWork));
    }

    @Override
    public void update(int completed) {
        //
    }

    @Override
    public void endTask() {
        Log.debug(SimpleProgressMonitor.class.getName(), "Task done!");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
