package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.common.api.exception.HotCloudException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public final class BuildPackStatus {

    private BuildPackStatus() {
    }

    public static JobStatus status(Job job) {
        Integer ready = job.getStatus().getReady();
        Integer active = job.getStatus().getActive();
        Integer succeeded = job.getStatus().getSucceeded();
        Integer failed = job.getStatus().getFailed();

        if (ready != null && Objects.equals(ready, 1)) {
            return JobStatus.Ready;
        }

        if (active != null && Objects.equals(active, 1)) {
            return JobStatus.Active;
        }

        if (succeeded != null && Objects.equals(succeeded, 1)) {
            return JobStatus.Succeeded;
        }

        if (failed != null) {
            log.error("JobStatus: {}", job.getStatus());
            return JobStatus.Failed;
        }

        log.error("JobStatus: {}", job.getStatus());
        throw new HotCloudException("Unknown job status!");
    }

    public enum JobStatus {
        //
        Ready,
        Active,
        Succeeded,
        Failed
    }
}
