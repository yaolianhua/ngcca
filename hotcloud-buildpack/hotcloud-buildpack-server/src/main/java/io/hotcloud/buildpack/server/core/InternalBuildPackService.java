package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.kubernetes.api.configurations.SecretBuilder;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeBuilder;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimBuilder;
import io.hotcloud.kubernetes.api.workload.JobBuilder;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.ImagePullPolicy;
import io.hotcloud.kubernetes.model.pod.container.VolumeMount;
import io.hotcloud.kubernetes.model.storage.*;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.hotcloud.kubernetes.model.workload.JobSpec;
import io.hotcloud.kubernetes.model.workload.JobTemplate;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
class InternalBuildPackService extends AbstractBuildPackApi {

    private final BuildPackStorageProperties storageProperties;
    private final KanikoFlag kanikoFlag;

    public InternalBuildPackService(BuildPackStorageProperties storageProperties,
                                    KanikoFlag kanikoFlag) {
        this.storageProperties = storageProperties;
        this.kanikoFlag = kanikoFlag;
    }

    @Override
    protected String yaml(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack is null");
        Assert.notNull(buildPack.getJobResource(), "BuildPack job resource is null");
        Assert.notNull(buildPack.getSecretResource(), "BuildPack docker secret resource is null");
        Assert.notNull(buildPack.getStorageResource(), "BuildPack storage resourceList is null");

        Assert.hasText(buildPack.getJobResource().getJobResourceYaml(), "BuildPack job resource yaml is null");
        Assert.hasText(buildPack.getStorageResource().getResourceListYaml(), "BuildPack storage resource yaml is null");
        Assert.hasText(buildPack.getSecretResource().getSecretResourceYaml(), "BuildPack docker secret resource yaml is null");

        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(buildPack.getJobResource().getJobResourceYaml());
        stringBuilder.append("---\n");
        stringBuilder.append(buildPack.getStorageResource().getResourceListYaml());
        stringBuilder.append("---\n");
        stringBuilder.append(buildPack.getSecretResource().getSecretResourceYaml());
        return stringBuilder.toString();
    }

    @Override
    protected BuildPackJobResource jobResource(BuildPackJobResourceInternalInput resource) {
        Assert.notNull(resource, "buildpack job resource request body is null");
        Assert.hasText(resource.getNamespace(), "namespace is null");
        Assert.hasText(resource.getPersistentVolumeClaim(), "pvc is null");
        Assert.hasText(resource.getSecret(), "secret name is null");

        Map<String, String> alternative = resource.getAlternative();
        String project = alternative.get(BuildPackConstant.GIT_PROJECT_NAME);

        Map<String, String> labels = Map.of(BuildPackConstant.K8S_APP, project + "-" + resource.getNamespace());
        String jobName = String.format("%s-job-buildpack-%s", project, resource.getNamespace());

        JobCreateRequest request = new JobCreateRequest();
        ObjectMetadata jobMetadata = new ObjectMetadata();
        jobMetadata.setNamespace(resource.getNamespace());
        jobMetadata.setName(jobName);
        jobMetadata.setLabels(labels);
        request.setMetadata(jobMetadata);

        JobTemplate template = new JobTemplate();

        PodTemplateSpec templateSpec = new PodTemplateSpec();

        Container container = new Container();
        container.setName(BuildPackConstant.KANIKO_CONTAINER);
        container.setImage(BuildPackConstant.KANIKO_IMAGE);
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        List<String> finalArgs = resource.getArgs()
                .entrySet()
                .stream()
                .map(e -> String.format("--%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        container.setArgs(finalArgs);

        //volume mounts
        VolumeMount dockersecretVolumeMount = VolumeMount.of(BuildPackConstant.DOCKER_SECRET_VOLUME, "/kaniko/.docker", true);
        VolumeMount workspaceVolumeMount = VolumeMount.of(BuildPackConstant.WORKSPACE_VOLUME, kanikoFlag.getContext(), false);
        container.setVolumeMounts(List.of(dockersecretVolumeMount, workspaceVolumeMount));

        //volumes
        SecretVolume secretVolumeType = new SecretVolume();
        secretVolumeType.setSecretName(resource.getSecret());
        secretVolumeType.setItems(List.of(SecretVolume.Item.of(BuildPackConstant.DOCKER_CONFIG_JSON, "config.json")));
        Volume dockersecretVolume = new Volume();
        dockersecretVolume.setName(BuildPackConstant.DOCKER_SECRET_VOLUME);
        dockersecretVolume.setSecretVolume(secretVolumeType);

        PersistentVolumeClaimVolume persistentVolumeClaimVolume = new PersistentVolumeClaimVolume();
        persistentVolumeClaimVolume.setClaimName(resource.getPersistentVolumeClaim());
        Volume workspaceVolume = new Volume();
        workspaceVolume.setPersistentVolumeClaim(persistentVolumeClaimVolume);
        workspaceVolume.setName(BuildPackConstant.WORKSPACE_VOLUME);
        //set volumes
        templateSpec.setContainers(List.of(container));
        templateSpec.setVolumes(List.of(dockersecretVolume, workspaceVolume));
        templateSpec.setRestartPolicy(PodTemplateSpec.RestartPolicy.Never);

        template.setSpec(templateSpec);

        ObjectMetadata podMetadata = new ObjectMetadata();
        podMetadata.setLabels(labels);
        template.setMetadata(podMetadata);

        JobSpec spec = new JobSpec();
        spec.setTtlSecondsAfterFinished(600);
        spec.setBackoffLimit(1);
        spec.setActiveDeadlineSeconds(3600L);
        spec.setTemplate(template);

        request.setSpec(spec);

        String jobYaml = Yaml.dump(JobBuilder.build(request));
        return BuildPackJobResource.builder()
                .name(jobName)
                .namespace(resource.getNamespace())
                .labels(labels)
                .args(resource.getArgs())
                .alternative(alternative)
                .jobResourceYaml(jobYaml)
                .build();
    }

    @Override
    protected BuildPackStorageResourceList storageResourceList(BuildPackStorageResourceInternalInput resource) {
        Assert.notNull(resource, "buildpack storage resource request body is null");
        Assert.hasText(resource.getNamespace(), "namespace is null");

        String gitProject = resource.getAlternative().get(BuildPackConstant.GIT_PROJECT_NAME);
        String gitProjectPath = resource.getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
        Assert.hasText(gitProjectPath, "data volume path is null");

        String pvName = StringUtils.hasText(resource.getPersistentVolume()) ? resource.getPersistentVolume() : "pv-" + gitProject + "-" + resource.getNamespace();
        String pvcName = StringUtils.hasText(resource.getPersistentVolumeClaim()) ? resource.getPersistentVolumeClaim() : "pvc-" + gitProject + "-" + resource.getNamespace();
        String capacity = "1Gi";

        List<String> accessModes = List.of("ReadWriteOnce");
        Map<String, String> storage = Map.of("storage", capacity);


        //pv
        PersistentVolumeCreateRequest persistentVolumeCreateRequest = new PersistentVolumeCreateRequest();
        ObjectMetadata pvMetadata = new ObjectMetadata();
        pvMetadata.setName(pvName);
        //nothing affect
        pvMetadata.setNamespace(resource.getNamespace());
        persistentVolumeCreateRequest.setMetadata(pvMetadata);

        PersistentVolumeSpec persistentVolumeSpec = new PersistentVolumeSpec();
        persistentVolumeSpec.setCapacity(storage);
        persistentVolumeSpec.setAccessModes(accessModes);
        persistentVolumeSpec.setStorageClassName(BuildPackConstant.STORAGE_CLASS);
        persistentVolumeSpec.setVolumeMode(PersistentVolumeSpec.VolumeMode.Filesystem);
        persistentVolumeSpec.setPersistentVolumeReclaimPolicy(PersistentVolumeSpec.ReclaimPolicy.Retain);
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            HostPathVolume hostPathVolume = HostPathVolume.of(gitProjectPath, null);
            persistentVolumeSpec.setHostPath(hostPathVolume);
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            NFSVolume nfsVolume = NFSVolume.of(gitProjectPath, storageProperties.getNfsServer(), false);
            persistentVolumeSpec.setNfs(nfsVolume);
        }
        PersistentVolumeSpec.ClaimRef claimRef = new PersistentVolumeSpec.ClaimRef();
        claimRef.setNamespaces(resource.getNamespace());
        claimRef.setName(pvcName);
        persistentVolumeSpec.setClaimRef(claimRef);

        persistentVolumeCreateRequest.setSpec(persistentVolumeSpec);

        //pvc
        PersistentVolumeClaimCreateRequest persistentVolumeClaimCreateRequest = new PersistentVolumeClaimCreateRequest();
        ObjectMetadata pvcMetadata = new ObjectMetadata();
        pvcMetadata.setName(pvcName);
        pvcMetadata.setNamespace(resource.getNamespace());

        persistentVolumeClaimCreateRequest.setMetadata(pvcMetadata);

        PersistentVolumeClaimSpec persistentVolumeClaimSpec = new PersistentVolumeClaimSpec();
        persistentVolumeClaimSpec.setVolumeMode(PersistentVolumeClaimSpec.VolumeMode.Filesystem);
        persistentVolumeClaimSpec.setStorageClassName(BuildPackConstant.STORAGE_CLASS);
        persistentVolumeClaimSpec.setAccessModes(accessModes);
        persistentVolumeClaimSpec.setResources(Resources.ofRequest(storage));
        persistentVolumeClaimSpec.setVolumeName(pvName);

        persistentVolumeClaimCreateRequest.setSpec(persistentVolumeClaimSpec);

        //yaml
        String pvYaml = Yaml.dump(PersistentVolumeBuilder.build(persistentVolumeCreateRequest));
        String pvcYaml = Yaml.dump(PersistentVolumeClaimBuilder.build(persistentVolumeClaimCreateRequest));

        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(pvYaml);
        stringBuilder.append("---\n");
        stringBuilder.append(pvcYaml);

        return BuildPackStorageResourceList.builder()
                .resourceListYaml(stringBuilder.toString())
                .namespace(resource.getNamespace())
                .persistentVolumeClaim(pvcName)
                .persistentVolume(pvName)
                .storageClass(BuildPackConstant.STORAGE_CLASS)
                .capacity(capacity)
                .alternative(resource.getAlternative())
                .build();
    }

    @Override
    protected BuildPackDockerSecretResource dockersecret(BuildPackDockerSecretResourceInternalInput resource) {
        Assert.notNull(resource, "buildpack docker secret resource request body is null");
        Assert.hasText(resource.getNamespace(), "namespace is null");

        String gitProject = resource.getAlternative().get(BuildPackConstant.GIT_PROJECT_NAME);
        String name = StringUtils.hasText(resource.getName()) ? resource.getName() : "secret-" + gitProject + "-" + resource.getNamespace();
        Map<String, String> labels = Map.of(BuildPackConstant.K8S_APP, gitProject + "-" + resource.getNamespace());

        SecretCreateRequest request = new SecretCreateRequest();
        request.setImmutable(false);
        request.setType("kubernetes.io/dockerconfigjson");

        Map<String, String> data = Map.of(BuildPackConstant.DOCKER_CONFIG_JSON, resource.dockerconfigjson());
        request.setData(data);

        ObjectMetadata secretMetadata = new ObjectMetadata();
        secretMetadata.setName(name);
        secretMetadata.setNamespace(resource.getNamespace());
        secretMetadata.setLabels(labels);

        request.setMetadata(secretMetadata);

        String secretYaml = Yaml.dump(SecretBuilder.build(request));

        return BuildPackDockerSecretResource.builder()
                .data(data)
                .labels(labels)
                .name(name)
                .namespace(resource.getNamespace())
                .alternative(resource.getAlternative())
                .secretResourceYaml(secretYaml)
                .build();
    }
}
