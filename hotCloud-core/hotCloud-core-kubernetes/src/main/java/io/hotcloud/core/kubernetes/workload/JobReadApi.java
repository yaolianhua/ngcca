package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface JobReadApi {

    default Job read(String namespace, String job) {
        JobList jobList = this.read(namespace);
        return jobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), job))
                .findFirst()
                .orElse(null);
    }

    default JobList read() {
        return this.read(null);
    }

    default JobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    JobList read(String namespace, Map<String, String> labelSelector);
}
