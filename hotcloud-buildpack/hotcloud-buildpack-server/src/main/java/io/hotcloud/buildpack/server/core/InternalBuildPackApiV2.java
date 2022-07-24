package io.hotcloud.buildpack.server.core;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.api.core.AbstractBuildPackApiV2;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackDockerSecretResource;
import io.hotcloud.buildpack.api.core.BuildPackJobResource;
import io.hotcloud.common.api.Log;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.api.workload.JobApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static io.hotcloud.buildpack.api.core.TemplateRender.*;

@Service
@RequiredArgsConstructor
@Slf4j
class InternalBuildPackApiV2 extends AbstractBuildPackApiV2 {

    private final KubectlApi kubectlApi;
    private final JobApi jobApi;
    private final PodApi podApi;
    private final BuildPackRegistryProperties registryProperties;
    private final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    @Override
    protected BuildPackJobResource prepareJob(String namespace, String httpGitUrl, String branch) {

        Assert.hasText(httpGitUrl, "Http git url is null");
        Assert.state(!CHINESE_PATTERN.matcher(httpGitUrl).find(), "Git url contains chinese char");
        Assert.state(!CHINESE_PATTERN.matcher(branch).find(), "Git branch contains chinese char");

        String substring = httpGitUrl.substring(httpGitUrl.lastIndexOf("/"));
        String originString = substring.substring(1, substring.length() - ".git".length());

        String project = originString.toLowerCase().replaceAll("_", "-");
        String resolvedBranch = branch.toLowerCase().replaceAll("_", "-");

        String k8sName = String.format("%s-%s-%s", namespace, project, resolvedBranch);

        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String image = String.format("%s:%s", k8sName, date);

        String artifactUrl = String.format("%s/%s/%s", registryProperties.getUrl(), registryProperties.getProject(), image);
        String job = kanikoJob(
                namespace,
                k8sName,
                k8sName,
                retrieveSecretName(namespace),
                artifactUrl,
                registryProperties.getKanikoImageUrl(),
                branch,
                httpGitUrl,
                registryProperties.getGitInitContainerImageUrl());


        BuildPackJobResource jobResource = BuildPackJobResource.builder()
                .labels(Map.of(BuildPackConstant.K8S_APP, k8sName))
                .jobResourceYaml(job)
                .name(k8sName)
                .namespace(namespace)
                .build();

        Map<String, String> alternative = jobResource.getAlternative();
        alternative.put(BuildPackConstant.IMAGEBUILD_ARTIFACT, artifactUrl);

        return jobResource;

    }

    @Override
    protected BuildPackDockerSecretResource prepareSecret(String namespace) {
        String dockerconfigjson = dockerconfigjson(
                registryProperties.getUrl(),
                registryProperties.getUsername(),
                registryProperties.getPassword(),
                true);

        String k8sName = retrieveSecretName(namespace);

        String secret = secretOfDockerconfigjson(namespace, k8sName, k8sName, dockerconfigjson);

        return BuildPackDockerSecretResource.builder()
                .namespace(namespace)
                .labels(Map.of(BuildPackConstant.K8S_APP, k8sName))
                .data(Map.of(BuildPackConstant.DOCKER_CONFIG_JSON, dockerconfigjson))
                .name(k8sName)
                .secretResourceYaml(secret)
                .build();
    }

    private static String retrieveSecretName(String namespace){
        return String.format("kaniko-%s", namespace);
    }

    @Override
    protected void doApply(String yaml) {
        List<HasMetadata> metadataList = kubectlApi.apply(null, yaml);
        for (HasMetadata hasMetadata : metadataList) {
            Log.info(InternalBuildPackApiV2.class.getName(),
                    String.format("%s '%s'", hasMetadata.getKind(), hasMetadata.getMetadata().getName()));
        }
    }

    @Override
    public KanikoStatus getStatus(String namespace, String job) {
        Job readJob = jobApi.read(namespace, job);
        if (Objects.isNull(readJob)) {
            return KanikoStatus.Unknown;
        }
        Integer active = readJob.getStatus().getActive();
        Integer failed = readJob.getStatus().getFailed();
        Integer ready = readJob.getStatus().getReady();
        Integer succeeded = readJob.getStatus().getSucceeded();

        if (ready != null && Objects.equals(ready, 1)) {
            return KanikoStatus.Ready;
        }

        if (active != null && Objects.equals(active, 1)) {
            return KanikoStatus.Active;
        }

        if (succeeded != null && Objects.equals(succeeded, 1)) {
            return KanikoStatus.Succeeded;
        }

        if (failed != null) {
            return KanikoStatus.Failed;
        }

        return KanikoStatus.Unknown;
    }

    @Override
    public String fetchLog(String namespace, String job) {

        Job kanikoJob = jobApi.read(namespace, job);
        if (Objects.isNull(kanikoJob)) {
            Log.info(InternalBuildPackApiV2.class.getName(),
                    String.format("Fetch kaniko log error. job is null namespace:%s job:%s", namespace, job));
            return "";
        }

        List<Pod> pods = podApi.read(namespace, kanikoJob.getMetadata().getLabels()).getItems();
        if (CollectionUtils.isEmpty(pods)) {
            Log.info(InternalBuildPackApiV2.class.getName(),
                    String.format("Fetch kaniko log error. list pods is empty namespace:%s job:%s", namespace, job));
            return "";
        }

        Pod pod = pods.get(0);
        try {
            return podApi.logs(namespace, pod.getMetadata().getName(), BuildPackConstant.KANIKO_CONTAINER,100);
        } catch (Exception e) {
            try {
                return podApi.logs(namespace, pod.getMetadata().getName(), BuildPackConstant.KANIKO_INIT_CONTAINER, 100);
            } catch (Exception e2) {
                Log.info(InternalBuildPackApiV2.class.getName(),
                        String.format("Fetch kaniko init container log error. %s", e2.getMessage()));
                return "";
            }
        }
    }
}
