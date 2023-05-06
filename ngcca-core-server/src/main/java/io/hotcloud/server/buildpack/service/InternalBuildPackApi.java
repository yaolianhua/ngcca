package io.hotcloud.server.buildpack.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.common.model.JavaRuntime;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.common.utils.INet;
import io.hotcloud.common.utils.Log;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.common.utils.Validator;
import io.hotcloud.kubernetes.client.http.JobClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.AbstractBuildPackApi;
import io.hotcloud.module.buildpack.model.*;
import io.hotcloud.server.SystemRegistryImageProperties;
import io.hotcloud.server.files.FileHelper;
import io.hotcloud.server.registry.RegistryProperties;
import io.hotcloud.vendor.kaniko.model.DockerConfigJson;
import io.hotcloud.vendor.kaniko.model.DockerfileJavaArtifactExpressionVariable;
import io.hotcloud.vendor.kaniko.model.KanikoJobExpressionVariable;
import io.hotcloud.vendor.kaniko.model.SecretExpressionVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static io.hotcloud.common.model.CommonConstant.K8S_APP;
import static io.hotcloud.common.model.CommonConstant.K8S_APP_BUSINESS_DATA_ID;
import static io.hotcloud.vendor.kaniko.DockerfileTemplateRender.DockerfileJava;
import static io.hotcloud.vendor.kaniko.KanikoJobTemplateRender.parseJob;
import static io.hotcloud.vendor.kaniko.KanikoJobTemplateRender.parseSecret;

@Service
@RequiredArgsConstructor
@Slf4j
class InternalBuildPackApi extends AbstractBuildPackApi {

    private final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
    private final KubectlClient kubectlApi;
    private final JobClient jobApi;
    private final PodClient podApi;
    private final RegistryProperties registryProperties;
    private final SystemRegistryImageProperties systemRegistryImageProperties;

    private static Map<String, List<String>> resolvedHostAliases(String registry, String httpUrl) {
        Map<String, List<String>> hostAliases = new HashMap<>(8);

        String registryHost = INet.getHost("http://" + registry);
        String httpUrlHost = INet.getHost(httpUrl);

        String localizedIPv4 = INet.getLocalizedIPv4();
        Assert.isTrue(!Objects.equals("127.0.0.1", localizedIPv4), "localized ip error");
        Assert.isTrue(!Objects.equals(registryHost, httpUrlHost), "host is error");

        String registryIpv4 = INet.getIPv4(registryHost);
        String httpUrlIpv4 = INet.getIPv4(httpUrlHost);

        if (Objects.equals(registryIpv4, httpUrlIpv4)) {
            hostAliases.put(registryIpv4, List.of(registryHost, httpUrlHost));
        } else {
            hostAliases.put(registryIpv4, List.of(registryHost));
            hostAliases.put(httpUrlIpv4, List.of(httpUrlHost));
        }

        Map<String, List<String>> resolvedHostAliases = new HashMap<>(8);
        for (Map.Entry<String, List<String>> entry : hostAliases.entrySet()) {
            if (Objects.equals("127.0.0.1", entry.getKey())) {
                resolvedHostAliases.put(localizedIPv4, entry.getValue());
                continue;
            }
            resolvedHostAliases.put(entry.getKey(), entry.getValue());
        }

        return resolvedHostAliases;
    }

    private static String resolvedK8sName(BuildImage buildImage) {
        if (buildImage.isSourceCode()) {
            Assert.hasText(buildImage.getSource().getHttpGitUrl(), "Http git url is null");
            Assert.isTrue(!CHINESE_PATTERN.matcher(buildImage.getSource().getHttpGitUrl()).find(), "Git url contains chinese char");
            Assert.isTrue(!CHINESE_PATTERN.matcher(buildImage.getSource().getBranch()).find(), "Git branch contains chinese char");

            //https://git.docker.local/self-host/thymeleaf-fragments.git --> /thymeleaf-fragments.git
            String substringWithSlash = buildImage.getSource().getHttpGitUrl().substring(buildImage.getSource().getHttpGitUrl().lastIndexOf("/"));
            // /thymeleaf-fragments.git --> thymeleaf-fragments
            String originProjectString = substringWithSlash.substring(1, substringWithSlash.length() - ".git".length());

            String resolvedProject = originProjectString.toLowerCase().replaceAll("_", "-");
            String resolvedBranch = buildImage.getSource().getBranch().toLowerCase().replaceAll("_", "-");

            Assert.isTrue(Validator.validK8sName(resolvedProject), "git project name is illegal [" + resolvedProject + "]");
            Assert.isTrue(Validator.validK8sName(resolvedBranch), "git branch is illegal [" + resolvedBranch + "]");
            return String.format("%s-%s-%s", resolvedProject, resolvedBranch, System.currentTimeMillis());
        }

        if (buildImage.isJar() || buildImage.isWar()) {
            String packageUrl = buildImage.isJar() ? buildImage.getJar().getPackageUrl() : buildImage.getWar().getPackageUrl();
            Assert.hasText(packageUrl, "Binary package url is null");
            String filename = FileHelper.getFilename(packageUrl);
            Assert.isTrue(Validator.validK8sName(filename), "Binary package name is illegal [" + filename + "]");

            return String.format("%s-%s", filename, System.currentTimeMillis());
        }

        throw new UnsupportedOperationException("Not supported operation for BuildImage");
    }

    private static String retrieveSecretName(String namespace) {
        return String.format("kaniko-%s", namespace);
    }

    @Override
    protected BuildPackJobResource prepareJobResource(String namespace, BuildImage buildImage) {
        KanikoJobExpressionVariable expressionVariable = determinedKanikoJobExpressionVariable(buildImage, namespace);
        String jobYamlString = parseJob(expressionVariable);

        BuildPackJobResource jobResource = BuildPackJobResource.builder()
                .labels(Map.of(K8S_APP, expressionVariable.getJob(), K8S_APP_BUSINESS_DATA_ID, expressionVariable.getBusinessId()))
                .jobResourceYaml(jobYamlString)
                .name(expressionVariable.getJob())
                .namespace(namespace)
                .build();

        Map<String, String> alternative = jobResource.getAlternative();
        alternative.put(BuildPackConstant.IMAGEBUILD_ARTIFACT, expressionVariable.getDestination());

        return jobResource;
    }

    private DockerfileJavaArtifactExpressionVariable determinedDockerfileJavaArtifactExpressionVariable(BuildImage buildImage) {
        if (buildImage.isSourceCode()) {
            String jarPath = StringUtils.hasText(buildImage.getSource().getSubmodule()) ? buildImage.getSource().getSubmodule() + "/target/*.jar" : "target/*.jar";
            JavaRuntime runtime = buildImage.getSource().getRuntime();
            String maven;
            switch (runtime) {
                case JAVA8 -> maven = systemRegistryImageProperties.getMaven3808();
                case JAVA11 -> maven = systemRegistryImageProperties.getMaven3811();
                case JAVA17 -> maven = systemRegistryImageProperties.getMaven3817();
                default -> throw new NGCCAPlatformException("Unsupported runtime");
            }
            return DockerfileJavaArtifactExpressionVariable.ofMavenJar(
                    maven,
                    systemRegistryImageProperties.getPropertyValue(runtime.name().toLowerCase()),
                    jarPath,
                    buildImage.getSource().getStartOptions(),
                    buildImage.getSource().getStartArgs());
        }

        if (buildImage.isJar() || buildImage.isWar()) {
            String httpUrl = buildImage.isJar() ? buildImage.getJar().getPackageUrl() : buildImage.getWar().getPackageUrl();
            return buildImage.isJar() ?
                    DockerfileJavaArtifactExpressionVariable.ofUrlJar(systemRegistryImageProperties.getPropertyValue(buildImage.getJar().getRuntime().name().toLowerCase()), httpUrl, buildImage.getJar().getStartOptions(), buildImage.getJar().getStartArgs()) :
                    DockerfileJavaArtifactExpressionVariable.ofUrlWar(systemRegistryImageProperties.getPropertyValue(buildImage.getWar().getRuntime().name().toLowerCase()), httpUrl);
        }

        throw new UnsupportedOperationException("Not supported operation for BuildImage");
    }

    private KanikoJobExpressionVariable determinedKanikoJobExpressionVariable(BuildImage buildImage,
                                                                              String namespace) {

        String k8sName = resolvedK8sName(buildImage);
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // image-name:image-tag
        String repo = String.format("%s:%s", k8sName, date);
        // harbor.local:5000/image-build-test/jenkins:20221220045021
        String destination = String.format("%s/%s/%s", registryProperties.getUrl(), registryProperties.getImagebuildNamespace(), repo);

        DockerfileJavaArtifactExpressionVariable javaArtifact = determinedDockerfileJavaArtifactExpressionVariable(buildImage);

        if (buildImage.isSourceCode()) {
            return KanikoJobExpressionVariable.of(
                    UUIDGenerator.uuidNoDash(),
                    namespace,
                    k8sName,
                    retrieveSecretName(namespace),
                    destination,
                    systemRegistryImageProperties.getKaniko(),
                    systemRegistryImageProperties.getAlpine(),
                    DockerfileJava(javaArtifact, true),
                    KanikoJobExpressionVariable.GitExpressionVariable.of(buildImage.getSource().getHttpGitUrl(), buildImage.getSource().getBranch(), systemRegistryImageProperties.getGit()),
                    resolvedHostAliases(registryProperties.getUrl(), buildImage.getSource().getHttpGitUrl())
            );
        }

        if (buildImage.isJar() || buildImage.isWar()) {
            String httpUrl = buildImage.isJar() ? buildImage.getJar().getPackageUrl() : buildImage.getWar().getPackageUrl();
            return KanikoJobExpressionVariable.of(
                    UUIDGenerator.uuidNoDash(),
                    namespace,
                    k8sName,
                    retrieveSecretName(namespace),
                    destination,
                    systemRegistryImageProperties.getKaniko(),
                    systemRegistryImageProperties.getAlpine(),
                    DockerfileJava(javaArtifact, true),
                    null,
                    resolvedHostAliases(registryProperties.getUrl(), httpUrl));
        }

        throw new UnsupportedOperationException("Not supported operation for BuildImage");
    }

    @Override
    protected BuildPackDockerSecretResource prepareSecretResource(String namespace) {

        String k8sName = retrieveSecretName(namespace);
        SecretExpressionVariable secretExpressionVariable = SecretExpressionVariable.of(
                namespace,
                k8sName,
                DockerConfigJson.of(registryProperties.getUrl(), registryProperties.getUsername(), registryProperties.getPassword())
        );

        String secret = parseSecret(secretExpressionVariable);

        return BuildPackDockerSecretResource.builder()
                .namespace(namespace)
                .labels(Map.of(K8S_APP, k8sName))
                .data(Map.of(BuildPackConstant.DOCKER_CONFIG_JSON, secretExpressionVariable.getDockerconfigjson()))
                .name(k8sName)
                .secretResourceYaml(secret)
                .build();
    }

    @Override
    protected void doApply(String yaml) {
        List<HasMetadata> metadataList = kubectlApi.resourceListCreateOrReplace(null, YamlBody.of(yaml));
        for (HasMetadata hasMetadata : metadataList) {
            Log.info(InternalBuildPackApi.class.getName(),
                    String.format("%s '%s' create or replace", hasMetadata.getKind(), hasMetadata.getMetadata().getName()));
        }
    }

    @Override
    public JobState getStatus(String namespace, String job) {
        Job readJob = jobApi.read(namespace, job);
        if (Objects.isNull(readJob)) {
            return JobState.UNKNOWN;
        }
        Integer active = readJob.getStatus().getActive();
        Integer failed = readJob.getStatus().getFailed();
        Integer ready = readJob.getStatus().getReady();
        Integer succeeded = readJob.getStatus().getSucceeded();

        if (ready != null && Objects.equals(ready, 1)) {
            return JobState.READY;
        }

        if (active != null && Objects.equals(active, 1)) {
            return JobState.ACTIVE;
        }

        if (succeeded != null && Objects.equals(succeeded, 1)) {
            return JobState.SUCCEEDED;
        }

        if (failed != null) {
            return JobState.FAILED;
        }

        return JobState.UNKNOWN;
    }

    @Override
    public String fetchLog(String namespace, String job) {

        Job kanikoJob = jobApi.read(namespace, job);
        if (Objects.isNull(kanikoJob)) {
            Log.info(InternalBuildPackApi.class.getName(),
                    String.format("Fetch kaniko log error. job is null namespace:%s job:%s", namespace, job));
            return "";
        }

        List<Pod> pods = podApi.readList(namespace, kanikoJob.getMetadata().getLabels()).getItems();
        if (CollectionUtils.isEmpty(pods)) {
            Log.info(InternalBuildPackApi.class.getName(),
                    String.format("Fetch kaniko log error. list pods is empty namespace:%s job:%s", namespace, job));
            return "";
        }

        Pod pod = pods.get(0);
        try {
            return podApi.logs(namespace, pod.getMetadata().getName(), BuildPackConstant.KANIKO_CONTAINER, 100);
        } catch (Exception e) {
            try {
                return podApi.logs(namespace, pod.getMetadata().getName(), BuildPackConstant.KANIKO_INIT_GIT_CONTAINER, 100);
            } catch (Exception e2) {
                try {
                    return podApi.logs(namespace, pod.getMetadata().getName(), BuildPackConstant.KANIKO_INIT_ALPINE_CONTAINER, 100);
                } catch (Exception e3) {
                    Log.info(InternalBuildPackApi.class.getName(),
                            String.format("Fetch kaniko init container log error. %s", e3.getMessage()));
                    return "";
                }
            }
        }
    }
}
