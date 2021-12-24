package io.hotcloud.kubernetes.api.workload;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
import io.hotcloud.kubernetes.model.workload.CronJobSpec;
import io.hotcloud.kubernetes.model.workload.JobSpec;
import io.kubernetes.client.openapi.models.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class CronJobBuilder {

    public static String API_VERSION = "batch/v1";
    public static String KIND = "CronJob";

    private CronJobBuilder() {
    }

    public static V1CronJob build(CronJobCreateRequest request) {

        V1CronJob v1CronJob = new V1CronJob();

        v1CronJob.setApiVersion(API_VERSION);
        v1CronJob.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = build(request.getMetadata());
        v1CronJob.setMetadata(v1ObjectMeta);

        V1CronJobSpec v1CronJobSpec = build(request.getSpec());
        v1CronJob.setSpec(v1CronJobSpec);

        return v1CronJob;
    }

    public static V1CronJobSpec build(CronJobSpec cronJobSpec) {

        V1CronJobSpec spec = new V1CronJobSpec();

        //build Template
        ObjectMetadata cronJobTemplateMetadata = cronJobSpec.getJobTemplate().getMetadata();
        JobSpec jobSpec = cronJobSpec.getJobTemplate().getSpec();
        V1JobTemplateSpec v1JobTemplateSpec = build(cronJobTemplateMetadata, jobSpec);

        spec.setJobTemplate(v1JobTemplateSpec);
        spec.setConcurrencyPolicy(cronJobSpec.getConcurrencyPolicy());
        spec.setFailedJobsHistoryLimit(cronJobSpec.getFailedJobsHistoryLimit());
        spec.setSchedule(cronJobSpec.getSchedule());
        spec.setStartingDeadlineSeconds(cronJobSpec.getStartingDeadlineSeconds());
        spec.setSuccessfulJobsHistoryLimit(cronJobSpec.getSuccessfulJobsHistoryLimit());
        spec.setSuspend(cronJobSpec.getSuspend());


        return spec;
    }

    private static V1JobTemplateSpec build(ObjectMetadata cronJobTemplateMetadata, JobSpec jobSpec) {
        V1JobTemplateSpec v1JobTemplateSpec = new V1JobTemplateSpec();

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setAnnotations(cronJobTemplateMetadata.getAnnotations());
        v1ObjectMeta.setLabels(cronJobTemplateMetadata.getLabels());

        v1JobTemplateSpec.setMetadata(v1ObjectMeta);

        V1JobSpec v1JobSpec = JobBuilder.build(jobSpec);
        v1JobTemplateSpec.setSpec(v1JobSpec);

        return v1JobTemplateSpec;
    }

    private static V1ObjectMeta build(ObjectMetadata jobMetadata) {
        String name = jobMetadata.getName();
        String namespace = jobMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "cronjob name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "cronjob namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(jobMetadata.getLabels());
        v1ObjectMeta.setAnnotations(jobMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }
}
