package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.api.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.GitApi;
import io.hotcloud.buildpack.api.KanikoFlag;
import io.hotcloud.buildpack.api.model.*;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Base64Helper;
import io.hotcloud.common.StringHelper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Service
public class BuildPackService extends AbstractBuildPackApi {

    private final BuildPackStorageProperties storageProperties;
    private final KanikoFlag kanikoFlag;
    private final GitApi gitApi;

    public BuildPackService(BuildPackStorageProperties storageProperties,
                            KanikoFlag kanikoFlag,
                            GitApi gitApi) {
        this.storageProperties = storageProperties;
        this.kanikoFlag = kanikoFlag;
        this.gitApi = gitApi;
    }

    @Override
    protected String yaml(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack is null", 400);
        Assert.notNull(buildPack.getJob(), "BuildPack job resource is null", 400);
        Assert.notNull(buildPack.getDockerSecret(), "BuildPack docker secret resource is null", 400);
        Assert.notNull(buildPack.getStorage(), "BuildPack storage resourceList is null", 400);

        Assert.hasText(buildPack.getJob().getJobResourceYaml(), "BuildPack job resource yaml is null", 400);
        Assert.hasText(buildPack.getStorage().getResourceListYaml(), "BuildPack storage resource yaml is null", 400);
        Assert.hasText(buildPack.getDockerSecret().getSecretResourceYaml(), "BuildPack docker secret resource yaml is null", 400);

        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(buildPack.getJob().getJobResourceYaml());
        stringBuilder.append("\n");
        stringBuilder.append("---\n");
        stringBuilder.append(buildPack.getStorage().getResourceListYaml());
        stringBuilder.append("\n");
        stringBuilder.append("---\n");
        stringBuilder.append(buildPack.getDockerSecret().getSecretResourceYaml());
        return stringBuilder.toString();
    }

    @Override
    protected BuildPackRepositoryCloned clone(BuildPackRepositoryCloneRequest input) {
        Assert.notNull(input, "BuildPack repository clone request body is null", 400);
        Assert.hasText(input.getRemote(), "Git url is null", 400);
        Assert.hasText(input.getLocal(), "Local path is null", 400);

        String project = StringHelper.retrieveProjectFromHTTPGitUrl(input.getRemote());
        Boolean cloned = gitApi.clone(input.getRemote(), input.getBranch(), Path.of(input.getLocal(), project).toString(), input.isForce(), input.getUsername(), input.getPassword());
        if (!cloned) {
            return null;
        }

        return BuildPackRepositoryCloned
                .builder()
                .local(input.getLocal())
                .remote(input.getRemote())
                .project(project)
                .build();
    }

    @Override
    protected BuildPackJobResource jobResource(BuildPackJobResourceRequest resource) {
        Assert.notNull(resource, "buildpack job resource request body is null", 400);
        Assert.hasText(resource.getNamespace(), "namespace is null", 400);
        Assert.hasText(resource.getPersistentVolumeClaim(), "pvc is null", 400);
        Assert.hasText(resource.getSecret(), "secret name is null", 400);

        String dockersecretvolume = "docker-registry-secret-volume";
        String workspacevolume = "workspace-volume";
        Map<String, String> labels = Map.of("k8s-app", resource.getNamespace());
        String jobName = "buildpack-job-" + resource.getNamespace();

        JobCreateRequest request = new JobCreateRequest();
        ObjectMetadata jobMetadata = new ObjectMetadata();
        jobMetadata.setNamespace(resource.getNamespace());
        jobMetadata.setName(jobName);
        jobMetadata.setLabels(labels);
        request.setMetadata(jobMetadata);

        JobTemplate template = new JobTemplate();

        PodTemplateSpec templateSpec = new PodTemplateSpec();

        Container container = new Container();
        container.setName("kaniko");
        container.setImage("gcr.io/kaniko-project/executor:latest");
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        List<String> finalArgs = resource.getArgs()
                .entrySet()
                .stream()
                .map(e -> String.format("--%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        container.setArgs(finalArgs);

        //volume mounts
        VolumeMount dockersecretVolumeMount = VolumeMount.of(dockersecretvolume, "/kaniko/.docker", true);
        VolumeMount workspaceVolumeMount = VolumeMount.of(workspacevolume, "/workspace", false);
        container.setVolumeMounts(List.of(dockersecretVolumeMount, workspaceVolumeMount));

        //volumes
        SecretVolume secretVolumeType = new SecretVolume();
        secretVolumeType.setSecretName(resource.getSecret());
        secretVolumeType.setItems(List.of(SecretVolume.Item.of(".dockerconfigjson", "config.json")));
        Volume dockersecretVolume = new Volume();
        dockersecretVolume.setName(dockersecretvolume);
        dockersecretVolume.setSecretVolume(secretVolumeType);

        PersistentVolumeClaimVolume persistentVolumeClaimVolume = new PersistentVolumeClaimVolume();
        persistentVolumeClaimVolume.setClaimName(resource.getPersistentVolumeClaim());
        Volume workspaceVolume = new Volume();
        workspaceVolume.setPersistentVolumeClaim(persistentVolumeClaimVolume);
        workspaceVolume.setName(workspacevolume);
        //set volumes
        templateSpec.setContainers(List.of(container));
        templateSpec.setVolumes(List.of(dockersecretVolume, workspaceVolume));
        templateSpec.setRestartPolicy(PodTemplateSpec.RestartPolicy.Never);

        template.setSpec(templateSpec);


        JobSpec spec = new JobSpec();
        spec.setTtlSecondsAfterFinished(600);
        spec.setBackoffLimit(3);
        spec.setActiveDeadlineSeconds(1800L);
        spec.setTemplate(template);

        request.setSpec(spec);

        String jobYaml = Yaml.dump(JobBuilder.build(request));
        return BuildPackJobResource.builder()
                .name(jobName)
                .namespace(resource.getNamespace())
                .labels(labels)
                .args(resource.getArgs())
                .jobResourceYaml(jobYaml)
                .build();
    }

    @Override
    protected BuildPackStorageResourceList storageResourceList(BuildPackStorageResourceRequest resource) {
        Assert.notNull(resource, "buildpack storage resource request body is null", 400);
        Assert.hasText(resource.getNamespace(), "namespace is null", 400);

        String pvName = StringUtils.hasText(resource.getPersistentVolume()) ? resource.getPersistentVolume() : "pv-" + resource.getNamespace();
        String pvcName = StringUtils.hasText(resource.getPersistentVolumeClaim()) ? resource.getPersistentVolumeClaim() : "pvc-" + resource.getNamespace();
        Integer capacity = null == resource.getSizeGb() ? storageProperties.getSizeGb() : resource.getSizeGb();
        String storageClass = storageProperties.getStorageClass().getName();
        List<String> accessModes = List.of("ReadWriteOnce");
        Map<String, String> storage = Map.of("storage", capacity + "Gi");


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
        persistentVolumeSpec.setStorageClassName(storageClass);
        persistentVolumeSpec.setVolumeMode(PersistentVolumeSpec.VolumeMode.Filesystem);
        persistentVolumeSpec.setPersistentVolumeReclaimPolicy(PersistentVolumeSpec.ReclaimPolicy.Delete);
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            HostPathVolume hostPathVolume = HostPathVolume.of(Path.of(storageProperties.getHostPath().getPath(), resource.getNamespace()).toString(), null);
            persistentVolumeSpec.setHostPath(hostPathVolume);
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            NFSVolume nfsVolume = NFSVolume.of(Path.of(storageProperties.getNfs().getPath(), resource.getNamespace()).toString(), storageProperties.getNfs().getServer(), false);
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
        persistentVolumeClaimSpec.setStorageClassName(storageClass);
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
                .storageClass(storageClass)
                .sizeGb(capacity)
                .build();
    }

    @Override
    protected BuildPackDockerSecretResource dockersecret(BuildPackDockerSecretResourceRequest resource) {
        Assert.notNull(resource, "buildpack docker secret resource request body is null", 400);
        Assert.hasText(resource.getNamespace(), "namespace is null", 400);

        String name = StringUtils.hasText(resource.getName()) ? resource.getName() : "secret-" + resource.getNamespace();
        Map<String, String> labels = Map.of("k8s-app", resource.getNamespace());

        SecretCreateRequest request = new SecretCreateRequest();
        request.setImmutable(true);
        request.setType("kubernetes.io/dockerconfigjson");

        String configjson = Base64Helper.dockerconfigjson(resource.getRegistry(), resource.getUsername(), resource.getPassword());
        Map<String, String> data = Map.of(".dockerconfigjson", configjson);
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
                .secretResourceYaml(secretYaml)
                .build();
    }
}
