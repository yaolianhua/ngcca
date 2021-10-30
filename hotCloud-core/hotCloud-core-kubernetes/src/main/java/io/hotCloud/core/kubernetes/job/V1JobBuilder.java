package io.hotCloud.core.kubernetes.job;

import io.hotCloud.core.common.Assert;
import io.hotCloud.core.kubernetes.V1LabelSelectorBuilder;
import io.hotCloud.core.kubernetes.pod.PodTemplateMetadata;
import io.hotCloud.core.kubernetes.pod.PodTemplateSpec;
import io.hotCloud.core.kubernetes.pod.V1PodTemplateSpecBuilder;
import io.kubernetes.client.openapi.models.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1JobBuilder {
    public static String API_VERSION = "batch/v1";
    public static String KIND = "Job";

    private V1JobBuilder() {
    }

    public static V1Job buildV1Job(JobCreateParams request) {

        V1Job v1Job = new V1Job();

        v1Job.setApiVersion(API_VERSION);
        v1Job.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = buildV1JobMetadata(request.getMetadata());
        v1Job.setMetadata(v1ObjectMeta);

        V1JobSpec v1JobSpec = buildV1JobSpec(request.getSpec());
        v1Job.setSpec(v1JobSpec);

        return v1Job;
    }

    public static V1JobSpec buildV1JobSpec(JobSpec jobSpec) {

        V1JobSpec spec = new V1JobSpec();

        //build selector
        V1LabelSelector v1LabelSelector = V1LabelSelectorBuilder.build(jobSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        PodTemplateMetadata podTemplateMetadata = jobSpec.getTemplate().getMetadata();
        PodTemplateSpec podTemplateSpec = jobSpec.getTemplate().getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = V1PodTemplateSpecBuilder.build(podTemplateMetadata, podTemplateSpec);
        spec.setTemplate(v1PodTemplateSpec);

        spec.setActiveDeadlineSeconds(jobSpec.getActiveDeadlineSeconds());
        spec.setBackoffLimit(jobSpec.getBackoffLimit());
        spec.setCompletions(jobSpec.getCompletions());
        spec.setParallelism(jobSpec.getParallelism());
        spec.setTtlSecondsAfterFinished(jobSpec.getTtlSecondsAfterFinished());
        spec.setManualSelector(jobSpec.getManualSelector());

        return spec;
    }

    private static V1ObjectMeta buildV1JobMetadata(JobMetadata jobMetadata) {
        String name = jobMetadata.getName();
        String namespace = jobMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "job name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "job namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(jobMetadata.getLabels());
        v1ObjectMeta.setAnnotations(jobMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }

}
