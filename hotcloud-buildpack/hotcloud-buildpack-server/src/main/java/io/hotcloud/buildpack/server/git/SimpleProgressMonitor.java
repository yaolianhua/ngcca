package io.hotcloud.buildpack.server.git;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.ProgressMonitor;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class SimpleProgressMonitor implements ProgressMonitor {

    @Override
    public void start(int totalTasks) {
        log.debug("Starting work on '{}' tasks", totalTasks);
    }

    @Override
    public void beginTask(String title, int totalWork) {
        log.debug("Start '{}': '{}'", title, totalWork);
    }

    @Override
    public void update(int completed) {
        //
    }

    @Override
    public void endTask() {
        log.debug("Task done!");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
