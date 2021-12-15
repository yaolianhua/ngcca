package io.hotcloud.core.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface CronJobReadApi {

    default CronJob read(String namespace, String cronjob) {
        CronJobList cronJobList = this.read(namespace);
        return cronJobList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), cronjob))
                .findFirst()
                .orElse(null);
    }

    default CronJobList read() {
        return this.read(null);
    }

    default CronJobList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    CronJobList read(String namespace, Map<String, String> labelSelector);
}
