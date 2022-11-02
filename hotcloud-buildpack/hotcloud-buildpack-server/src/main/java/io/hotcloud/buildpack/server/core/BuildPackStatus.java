package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.api.core.ImageBuildStatus;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.exception.HotCloudException;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class BuildPackStatus {

    private BuildPackStatus() {
    }

    public static ImageBuildStatus status(Job job) {
        Integer ready = job.getStatus().getReady();
        Integer active = job.getStatus().getActive();
        Integer succeeded = job.getStatus().getSucceeded();
        Integer failed = job.getStatus().getFailed();

        if (ready != null && Objects.equals(ready, 1)) {
            return ImageBuildStatus.Ready;
        }

        if (active != null && Objects.equals(active, 1)) {
            return ImageBuildStatus.Active;
        }

        if (succeeded != null && Objects.equals(succeeded, 1)) {
            return ImageBuildStatus.Succeeded;
        }

        if (failed != null) {
            Log.error(BuildPackStatus.class.getName(),
                    String.format("JobStatus: %s", job.getStatus()));
            return ImageBuildStatus.Failed;
        }

        Log.error(BuildPackStatus.class.getName(),
                String.format("JobStatus: %s", job.getStatus()));
        throw new HotCloudException("Unknown job status!");
    }
}
