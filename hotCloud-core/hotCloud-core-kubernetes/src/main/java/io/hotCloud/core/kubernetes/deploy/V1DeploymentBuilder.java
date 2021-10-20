package io.hotCloud.core.kubernetes.deploy;

import io.hotCloud.core.common.Assert;
import io.hotCloud.core.kubernetes.LabelSelector;
import io.hotCloud.core.kubernetes.affinity.*;
import io.hotCloud.core.kubernetes.pod.PodSecurityContext;
import io.hotCloud.core.kubernetes.pod.PodTemplateMetadata;
import io.hotCloud.core.kubernetes.pod.PodTemplateSpec;
import io.hotCloud.core.kubernetes.pod.container.*;
import io.hotCloud.core.kubernetes.volumes.Volume;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1DeploymentBuilder {
    private V1DeploymentBuilder() {
    }

    public static String API_VERSION = "apps/v1";
    public static String KIND = "Deployment";

    public static V1Deployment buildV1Deployment(DeploymentCreationParams request){

        V1Deployment v1Deployment = new V1Deployment();

        v1Deployment.setApiVersion(API_VERSION);
        v1Deployment.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = buildV1DeploymentMetadata(request.getMetadata());
        v1Deployment.setMetadata(v1ObjectMeta);

        V1DeploymentSpec v1DeploymentSpec = buildV1DeploymentSpec(request.getSpec());
        v1Deployment.setSpec(v1DeploymentSpec);

        return v1Deployment;
    }

    public static V1DeploymentSpec buildV1DeploymentSpec(DeploymentSpec deploymentSpec){

        V1DeploymentSpec spec = new V1DeploymentSpec();

        //build v1DeploymentStrategy
        V1DeploymentStrategy v1DeploymentStrategy = buildV1DeploymentStrategy(deploymentSpec.getStrategy());
        spec.setStrategy(v1DeploymentStrategy);

        //build selector
        V1LabelSelector v1LabelSelector = buildV1LabelSelector(deploymentSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        V1PodTemplateSpec v1PodTemplateSpec = buildV1PodTemplateSpec(deploymentSpec.getTemplate());
        spec.setTemplate(v1PodTemplateSpec);


        spec.setReplicas(deploymentSpec.getReplicas());
        spec.setMinReadySeconds(deploymentSpec.getMinReadySeconds());
        spec.setPaused(deploymentSpec.isPaused());
        spec.setProgressDeadlineSeconds(deploymentSpec.getProgressDeadlineSeconds());
        spec.setRevisionHistoryLimit(deploymentSpec.getRevisionHistoryLimit());

        return spec;
    }

    private static V1ObjectMeta buildV1DeploymentMetadata(DeploymentMetadata deploymentMetadata){
        String name = deploymentMetadata.getName();
        String namespace = deploymentMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "Deployment name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "Deployment namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(deploymentMetadata.getLabels());
        v1ObjectMeta.setAnnotations(deploymentMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }

    private static V1LabelSelector buildV1LabelSelector(LabelSelector labelSelector){

        V1LabelSelector v1LabelSelector = new V1LabelSelector();

        List<LabelSelector.LabelSelectorRequirement> matchExpressions = labelSelector.getMatchExpressions();

        List<V1LabelSelectorRequirement> requirements = matchExpressions.stream().map(e -> {
            V1LabelSelectorRequirement v1LabelSelectorRequirement = new V1LabelSelectorRequirement();
            v1LabelSelectorRequirement.setKey(e.getKey());
            v1LabelSelectorRequirement.setValues(e.getValues());
            v1LabelSelectorRequirement.setOperator(e.getOperator().name());
            return v1LabelSelectorRequirement;
        }).collect(Collectors.toList());

        v1LabelSelector.setMatchExpressions(requirements);
        v1LabelSelector.setMatchLabels(labelSelector.getMatchLabels());

        return v1LabelSelector;
    }

    private static V1DeploymentStrategy buildV1DeploymentStrategy(DeploymentStrategy deploymentStrategy){

        V1DeploymentStrategy strategy = new V1DeploymentStrategy();
        strategy.setType(deploymentStrategy.getType().name());

        DeploymentStrategy.RollingUpdate rollingUpdate = deploymentStrategy.getRollingUpdate();
        if (null != rollingUpdate){
            V1RollingUpdateDeployment rollingUpdateDeployment = new V1RollingUpdateDeployment();
            rollingUpdateDeployment.setMaxSurge(new IntOrString(rollingUpdate.getMaxSurge()));
            rollingUpdateDeployment.setMaxUnavailable(new IntOrString(rollingUpdate.getMaxUnavailable()));
            strategy.setRollingUpdate(rollingUpdateDeployment);
        }

        return strategy;
    }

    public static V1PodTemplateSpec buildV1PodTemplateSpec(DeploymentTemplate deploymentTemplate){

        PodTemplateMetadata podTemplateMetadata = deploymentTemplate.getMetadata();
        PodTemplateSpec podTemplateSpec = deploymentTemplate.getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();

        //build pod metadata
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(podTemplateMetadata.getLabels());
        v1ObjectMeta.setAnnotations(podTemplateMetadata.getAnnotations());
        v1PodTemplateSpec.setMetadata(v1ObjectMeta);

        //build pod Spec
        V1PodSpec v1PodSpec = new V1PodSpec();
        v1PodSpec.setTerminationGracePeriodSeconds(podTemplateSpec.getTerminationGracePeriodSeconds());
        v1PodSpec.setActiveDeadlineSeconds(podTemplateSpec.getActiveDeadlineSeconds());
        v1PodSpec.setHostname(podTemplateSpec.getHostname());
        v1PodSpec.setRestartPolicy(podTemplateSpec.getRestartPolicy().name());
        v1PodSpec.setDnsPolicy(podTemplateSpec.getDnsPolicy().name());
        v1PodSpec.setEnableServiceLinks(podTemplateSpec.getEnableServiceLinks());
        v1PodSpec.setAutomountServiceAccountToken(podTemplateSpec.getAutomountServiceAccountToken());
        v1PodSpec.setSubdomain(podTemplateSpec.getSubdomain());
        v1PodSpec.setShareProcessNamespace(podTemplateSpec.getShareProcessNamespace());
        v1PodSpec.setSetHostnameAsFQDN(podTemplateSpec.getSetHostnameAsFQDN());
        v1PodSpec.setServiceAccountName(podTemplateSpec.getServiceAccountName());
        v1PodSpec.setHostIPC(podTemplateSpec.getHostIPC());
        v1PodSpec.setHostNetwork(podTemplateSpec.getHostNetwork());
        v1PodSpec.setHostPID(podTemplateSpec.getHostPID());
        v1PodSpec.setPriority(podTemplateSpec.getPriority());
        v1PodSpec.setPriorityClassName(podTemplateSpec.getPriorityClassName());
        v1PodSpec.setSchedulerName(podTemplateSpec.getSchedulerName());
        v1PodSpec.setRuntimeClassName(podTemplateSpec.getRuntimeClassName());

        PodSecurityContext podSecurityContext = podTemplateSpec.getSecurityContext();
        if (Objects.nonNull(podSecurityContext)){
            V1PodSecurityContext v1PodSecurityContext = new V1PodSecurityContext();
            v1PodSecurityContext.setRunAsGroup(podSecurityContext.getRunAsGroup());
            v1PodSecurityContext.setRunAsUser(podSecurityContext.getRunAsUser());
            v1PodSecurityContext.setRunAsNonRoot(podSecurityContext.getRunAsNonRoot());
            v1PodSpec.setSecurityContext(v1PodSecurityContext);
        }

        List<V1Container> containers = podTemplateSpec.getContainers()
                .stream()
                .map(V1DeploymentBuilder::buildV1Container)
                .collect(Collectors.toList());
        v1PodSpec.setContainers(containers);

        List<V1Container> initContainers = podTemplateSpec.getInitContainers()
                .stream()
                .map(V1DeploymentBuilder::buildV1Container)
                .collect(Collectors.toList());
        v1PodSpec.setInitContainers(initContainers);

        List<V1Volume> v1Volumes = podTemplateSpec.getVolumes()
                .stream()
                .filter(Volume::exist)
                .map(V1DeploymentBuilder::buildV1Volume)
                .collect(Collectors.toList());
        v1PodSpec.setVolumes(v1Volumes);

        List<V1Toleration> v1Tolerations = podTemplateSpec.getTolerations().stream()
                .map(toleration -> {
                    V1Toleration v1Toleration = new V1Toleration();
                    v1Toleration.setEffect(toleration.getEffect().name());
                    v1Toleration.setOperator(toleration.getOperator().name());
                    v1Toleration.setKey(toleration.getKey());
                    v1Toleration.setTolerationSeconds(toleration.getTolerationSeconds());
                    v1Toleration.setValue(toleration.getValue());
                    return v1Toleration;
                }).collect(Collectors.toList());
        v1PodSpec.setTolerations(v1Tolerations);

        v1PodSpec.setNodeSelector(podTemplateSpec.getNodeSelector());

        Map<String, Quantity> overhead = new HashMap<>(32);
        podTemplateSpec.getOverhead().forEach((k,v) -> overhead.put(k,Quantity.fromString(v)));
        v1PodSpec.setOverhead(overhead);

        List<V1LocalObjectReference> localObjectReferences = podTemplateSpec.getImagePullSecrets().stream()
                .map(imagePullSecret -> {
                    V1LocalObjectReference v1LocalObjectReference = new V1LocalObjectReference();
                    v1LocalObjectReference.setName(imagePullSecret.getName());
                    return v1LocalObjectReference;
                }).collect(Collectors.toList());
        v1PodSpec.setImagePullSecrets(localObjectReferences);


        Affinity affinity = podTemplateSpec.getAffinity();
        if (Objects.nonNull(affinity)){
            V1Affinity v1Affinity = new V1Affinity();
            if (Objects.nonNull(affinity.getNodeAffinity())){
                V1NodeAffinity v1NodeAffinity = buildV1NodeAffinity(affinity.getNodeAffinity());
                v1Affinity.setNodeAffinity(v1NodeAffinity);
            }
            if (Objects.nonNull(affinity.getPodAffinity())){
                V1PodAffinity v1PodAffinity = buildV1PodAffinity(affinity.getPodAffinity());
                v1Affinity.setPodAffinity(v1PodAffinity);
            }
            if (Objects.nonNull(affinity.getPodAntiAffinity())){
                V1PodAntiAffinity v1PodAntiAffinity = buildV1PodAntiAffinity(affinity.getPodAntiAffinity());
                v1Affinity.setPodAntiAffinity(v1PodAntiAffinity);
            }
            v1PodSpec.setAffinity(v1Affinity);
        }


        v1PodTemplateSpec.setSpec(v1PodSpec);

        return v1PodTemplateSpec;
    }

    private static V1PodAntiAffinity buildV1PodAntiAffinity(PodAntiAffinity podAntiAffinity){

        V1PodAntiAffinity v1PodAntiAffinity = new V1PodAntiAffinity();
        List<V1PodAffinityTerm> v1PodAffinityTerms = buildV1PodAffinityTerms(podAntiAffinity.getRequiredDuringSchedulingIgnoredDuringExecution());
        v1PodAntiAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1PodAffinityTerms);

        List<WeightedPodAffinityTerm> weightedPodAffinityTerms = podAntiAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
        List<V1WeightedPodAffinityTerm> v1WeightedPodAffinityTerms = buildV1WeightedPodAffinityTerms(weightedPodAffinityTerms);
        v1PodAntiAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(v1WeightedPodAffinityTerms);

        return v1PodAntiAffinity;
    }

    private static List<V1WeightedPodAffinityTerm> buildV1WeightedPodAffinityTerms(List<WeightedPodAffinityTerm> weightedPodAffinityTerms) {
        return weightedPodAffinityTerms
                .stream()
                .map(weightedPodAffinityTerm -> {
                    V1WeightedPodAffinityTerm v1WeightedPodAffinityTerm = new V1WeightedPodAffinityTerm();
                    PodAffinityTerm podAffinityTerm = weightedPodAffinityTerm.getPodAffinityTerm();
                    V1PodAffinityTerm v1PodAffinityTerm = buildV1PodAffinityTerm(podAffinityTerm);

                    v1WeightedPodAffinityTerm.setWeight(weightedPodAffinityTerm.getWeight());
                    v1WeightedPodAffinityTerm.setPodAffinityTerm(v1PodAffinityTerm);
                    return v1WeightedPodAffinityTerm;
                }).collect(Collectors.toList());
    }

    private static List<V1PodAffinityTerm> buildV1PodAffinityTerms(List<PodAffinityTerm> podAffinityTerms) {
        return podAffinityTerms.stream()
                    .map(V1DeploymentBuilder::buildV1PodAffinityTerm)
                    .collect(Collectors.toList());
    }

    private static V1PodAffinity buildV1PodAffinity(PodAffinity podAffinity){
        V1PodAffinity v1PodAffinity = new V1PodAffinity();
        List<V1PodAffinityTerm> v1PodAffinityTerms = buildV1PodAffinityTerms(podAffinity.getRequiredDuringSchedulingIgnoredDuringExecution());
        v1PodAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1PodAffinityTerms);

        List<V1WeightedPodAffinityTerm> v1WeightedPodAffinityTerms = buildV1WeightedPodAffinityTerms(podAffinity.getPreferredDuringSchedulingIgnoredDuringExecution());
        v1PodAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(v1WeightedPodAffinityTerms);

        return v1PodAffinity;

    }

    private static V1PodAffinityTerm buildV1PodAffinityTerm(PodAffinityTerm podAffinityTerm) {
        List<V1LabelSelectorRequirement> v1LabelSelectorRequirements = podAffinityTerm.getLabelSelector().getMatchExpressions()
                .stream()
                .map(e -> {
                    V1LabelSelectorRequirement v1LabelSelectorRequirement = new V1LabelSelectorRequirement();
                    v1LabelSelectorRequirement.setKey(e.getKey());
                    v1LabelSelectorRequirement.setOperator(e.getOperator().name());
                    v1LabelSelectorRequirement.setValues(e.getValues());
                    return v1LabelSelectorRequirement;
                }).collect(Collectors.toList());

        V1LabelSelector v1LabelSelector = new V1LabelSelector();
        v1LabelSelector.setMatchExpressions(v1LabelSelectorRequirements);
        v1LabelSelector.setMatchLabels(podAffinityTerm.getLabelSelector().getMatchLabels());

        V1PodAffinityTerm v1PodAffinityTerm = new V1PodAffinityTerm();
        v1PodAffinityTerm.setNamespaces(podAffinityTerm.getNamespaces());
        v1PodAffinityTerm.setTopologyKey(podAffinityTerm.getTopologyKey());
        v1PodAffinityTerm.setLabelSelector(v1LabelSelector);
        return v1PodAffinityTerm;
    }

    private static V1NodeAffinity buildV1NodeAffinity(NodeAffinity nodeAffinity) {

        V1NodeAffinity v1NodeAffinity = new V1NodeAffinity();

        NodeSelector nodeSelector = nodeAffinity.getRequiredDuringSchedulingIgnoredDuringExecution();
        if (Objects.nonNull(nodeSelector)){
            V1NodeSelector v1NodeSelector = new V1NodeSelector();
            List<V1NodeSelectorTerm> v1NodeSelectorTerms = nodeSelector.getNodeSelectorTerms().stream()
                    .map(e -> {
                        V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
                        List<V1NodeSelectorRequirement> v1NodeSelectorRequirements = e.getMatchExpressions().stream()
                                .map(matchRequirement -> {
                                    V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                                    v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                                    v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                                    v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                                    return v1NodeSelectorRequirement;
                                }).collect(Collectors.toList());
                        v1NodeSelectorTerm.setMatchExpressions(v1NodeSelectorRequirements);

                        List<V1NodeSelectorRequirement> requirements = e.getMatchFields().stream()
                                .map(matchRequirement -> {
                                    V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                                    v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                                    v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                                    v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                                    return v1NodeSelectorRequirement;
                                }).collect(Collectors.toList());
                        v1NodeSelectorTerm.setMatchFields(requirements);
                        return v1NodeSelectorTerm;
                    }).collect(Collectors.toList());
            v1NodeSelector.setNodeSelectorTerms(v1NodeSelectorTerms);
            v1NodeAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1NodeSelector);
        }
        List<PreferredSchedulingTerm> preferredDuringSchedulingIgnoredDuringExecution = nodeAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
        List<V1PreferredSchedulingTerm> preferredSchedulingTerms = preferredDuringSchedulingIgnoredDuringExecution.stream()
                .filter(e -> Objects.nonNull(e.getPreference()))
                .map(e -> {
                    V1PreferredSchedulingTerm v1PreferredSchedulingTerm = new V1PreferredSchedulingTerm();

                    V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
                    List<V1NodeSelectorRequirement> v1NodeSelectorRequirements = e.getPreference().getMatchExpressions().stream()
                            .map(matchRequirement -> {
                                V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                                v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                                v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                                v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                                return v1NodeSelectorRequirement;
                            }).collect(Collectors.toList());
                    v1NodeSelectorTerm.setMatchExpressions(v1NodeSelectorRequirements);

                    List<V1NodeSelectorRequirement> requirements = e.getPreference().getMatchFields().stream()
                            .map(matchRequirement -> {
                                V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                                v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                                v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                                v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                                return v1NodeSelectorRequirement;
                            }).collect(Collectors.toList());
                    v1NodeSelectorTerm.setMatchFields(requirements);

                    v1PreferredSchedulingTerm.setPreference(v1NodeSelectorTerm);
                    v1PreferredSchedulingTerm.setWeight(e.getWeight());
                    return v1PreferredSchedulingTerm;

                }).collect(Collectors.toList());
        v1NodeAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(preferredSchedulingTerms);


        return v1NodeAffinity;
    }

    private static V1Volume buildV1Volume(Volume volume) {
        V1Volume v1Volume = new V1Volume();
        if (volume.isConfigMap()) {
            V1ConfigMapVolumeSource v1ConfigMapVolumeSource = new V1ConfigMapVolumeSource();
            v1ConfigMapVolumeSource.setName(volume.getConfigMap().getName());
            v1ConfigMapVolumeSource.setDefaultMode(volume.getConfigMap().getDefaultModel());
            v1ConfigMapVolumeSource.setOptional(volume.getConfigMap().isOptional());
            List<V1KeyToPath> v1KeyToPaths = volume.getConfigMap().getItems()
                    .stream()
                    .map(item -> {
                        V1KeyToPath v1KeyToPath = new V1KeyToPath();
                        v1KeyToPath.setKey(item.getKey());
                        v1KeyToPath.setMode(item.getMode());
                        v1KeyToPath.setPath(item.getPath());
                        return v1KeyToPath;
                    }).collect(Collectors.toList());

            v1ConfigMapVolumeSource.setItems(v1KeyToPaths);
            v1Volume.setConfigMap(v1ConfigMapVolumeSource);
        } else if (volume.isEmptyDir()) {
            V1EmptyDirVolumeSource v1EmptyDirVolumeSource = new V1EmptyDirVolumeSource();
            v1EmptyDirVolumeSource.setMedium(volume.getEmptyDir().getMedium());
            Quantity quantity = Quantity.fromString(volume.getEmptyDir().getSizeLimit());
            v1EmptyDirVolumeSource.setSizeLimit(quantity);
            v1Volume.setEmptyDir(v1EmptyDirVolumeSource);
        } else if (volume.isHostPath()) {
            V1HostPathVolumeSource v1HostPathVolumeSource = new V1HostPathVolumeSource();
            v1HostPathVolumeSource.setType(volume.getHostPath().getType());
            v1HostPathVolumeSource.setPath(volume.getHostPath().getPath());
            v1Volume.setHostPath(v1HostPathVolumeSource);
        } else if (volume.isNfs()) {
            V1NFSVolumeSource v1NFSVolumeSource = new V1NFSVolumeSource();
            v1NFSVolumeSource.setPath(volume.getNfs().getPath());
            v1NFSVolumeSource.setReadOnly(volume.getNfs().getReadOnly());
            v1NFSVolumeSource.setServer(volume.getNfs().getServer());
            v1Volume.setNfs(v1NFSVolumeSource);
        } else if (volume.isGitRepo()) {
            V1GitRepoVolumeSource v1GitRepoVolumeSource = new V1GitRepoVolumeSource();
            v1GitRepoVolumeSource.setDirectory(volume.getGitRepo().getDirectory());
            v1GitRepoVolumeSource.setRepository(volume.getGitRepo().getRepository());
            v1GitRepoVolumeSource.setRevision(volume.getGitRepo().getRevision());
            v1Volume.setGitRepo(v1GitRepoVolumeSource);
        }
        return v1Volume;
    }

    public static V1Container buildV1Container(Container container){

        V1Container v1Container = new V1Container();

        List<V1ContainerPort> v1ContainerPorts = buildV1ContainerPort(container.getPorts());
        v1Container.setPorts(v1ContainerPorts);

        Probe readinessProbe = container.getReadinessProbe();
        if (null != readinessProbe){
            V1Probe v1ReadinessProbe = buildV1Probe(readinessProbe);
            v1Container.setReadinessProbe(v1ReadinessProbe);
        }

        Probe livenessProbe = container.getLivenessProbe();
        if (null != livenessProbe){
            V1Probe v1LivenessProbe = buildV1Probe(livenessProbe);
            v1Container.setLivenessProbe(v1LivenessProbe);
        }

        Probe containerStartupProbe = container.getStartupProbe();
        if (null != containerStartupProbe){
            V1Probe startupProbe = buildV1Probe(containerStartupProbe);
            v1Container.setStartupProbe(startupProbe);
        }

        Resources resources = container.getResources();
        if (null != resources){
            V1ResourceRequirements v1ResourceRequirements = buildResources(resources);
            v1Container.setResources(v1ResourceRequirements);
        }

        List<V1EnvVar> v1EnvVars = buildEnv(container.getEnv());
        v1Container.setEnv(v1EnvVars);

        List<V1EnvFromSource> v1EnvFromSources = buildV1EnvFrom(container.getEnvFrom());
        v1Container.setEnvFrom(v1EnvFromSources);

        SecurityContext securityContext = container.getSecurityContext();
        if (Objects.nonNull(securityContext)){
            V1SecurityContext v1SecurityContext = new V1SecurityContext();
            v1SecurityContext.setRunAsNonRoot(securityContext.getRunAsNonRoot());
            v1SecurityContext.setRunAsUser(securityContext.getRunAsUser());
            v1SecurityContext.setRunAsGroup(securityContext.getRunAsGroup());
            v1SecurityContext.setPrivileged(securityContext.getPrivileged());
            v1Container.setSecurityContext(v1SecurityContext);
        }



        List<VolumeMount> volumeMounts = container.getVolumeMounts();
        List<V1VolumeMount> v1VolumeMounts = volumeMounts.stream().map(e -> {
            V1VolumeMount v1VolumeMount = new V1VolumeMount();
            v1VolumeMount.setMountPath(e.getMountPath());
            v1VolumeMount.setName(e.getName());
            v1VolumeMount.setReadOnly(e.getReadOnly());
            v1VolumeMount.setSubPath(e.getSubPath());
            v1VolumeMount.setSubPathExpr(e.getSubPathExpr());
            v1VolumeMount.setMountPropagation(e.getMountPropagation());
            return v1VolumeMount;
        }).collect(Collectors.toList());
        v1Container.setVolumeMounts(v1VolumeMounts);

        v1Container.setName(container.getName());
        v1Container.setImage(container.getImage());
        v1Container.setImagePullPolicy(container.getImagePullPolicy().name());
        v1Container.setArgs(container.getArgs());
        v1Container.setCommand(container.getCommand());

        return v1Container;

    }

    private static List<V1EnvFromSource> buildV1EnvFrom(List<EnvFrom> envFrom) {
        return envFrom.stream().map(e -> {
                V1EnvFromSource v1EnvFromSource = new V1EnvFromSource();
                v1EnvFromSource.setPrefix(e.getPrefix());

                EnvFrom.ConfigMapEnvSource configMapRef = e.getConfigMapRef();
                if (Objects.nonNull(configMapRef)){
                    V1ConfigMapEnvSource v1ConfigMapEnvSource = new V1ConfigMapEnvSource();
                    v1ConfigMapEnvSource.setName(configMapRef.getName());
                    v1ConfigMapEnvSource.setOptional(configMapRef.getOptional());
                    v1EnvFromSource.setConfigMapRef(v1ConfigMapEnvSource);
                }
                EnvFrom.SecretEnvSource secretRef = e.getSecretRef();
                if (Objects.nonNull(secretRef)) {
                    V1SecretEnvSource v1SecretEnvSource = new V1SecretEnvSource();
                    v1SecretEnvSource.setName(secretRef.getName());
                    v1SecretEnvSource.setOptional(secretRef.getOptional());
                    v1EnvFromSource.setSecretRef(v1SecretEnvSource);
                }
                return v1EnvFromSource;
            }).collect(Collectors.toList());
    }

    private static List<V1EnvVar> buildEnv(List<Env> envs) {
        return envs.stream().map(env -> {
            V1EnvVar v1EnvVar = new V1EnvVar();
            v1EnvVar.setName(env.getName());
            v1EnvVar.setValue(env.getValue());

            EnvSource envSource = env.getValueFrom();
            if (Objects.nonNull(envSource)){
                V1EnvVarSource v1EnvVarSource = new V1EnvVarSource();
                EnvSource.ConfigMapKeySelector configMapKeyRef = envSource.getConfigMapKeyRef();
                if (Objects.nonNull(configMapKeyRef)){
                    V1ConfigMapKeySelector v1ConfigMapKeySelector = new V1ConfigMapKeySelector();
                    v1ConfigMapKeySelector.setKey(configMapKeyRef.getKey());
                    v1ConfigMapKeySelector.setName(configMapKeyRef.getName());
                    v1ConfigMapKeySelector.setOptional(configMapKeyRef.getOptional());
                    v1EnvVarSource.setConfigMapKeyRef(v1ConfigMapKeySelector);
                    v1EnvVar.setValueFrom(v1EnvVarSource);
                }
                EnvSource.SecretKeySelector secretKeyRef = envSource.getSecretKeyRef();
                if (Objects.nonNull(secretKeyRef)){
                    V1SecretKeySelector v1SecretKeySelector = new V1SecretKeySelector();
                    v1SecretKeySelector.setKey(secretKeyRef.getKey());
                    v1SecretKeySelector.setName(secretKeyRef.getName());
                    v1SecretKeySelector.setOptional(secretKeyRef.getOptional());
                    v1EnvVarSource.setSecretKeyRef(v1SecretKeySelector);
                    v1EnvVar.setValueFrom(v1EnvVarSource);
                }
            }
            return v1EnvVar;
        }).collect(Collectors.toList());
    }

    private static V1ResourceRequirements buildResources(Resources resources) {

        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();

        Resources.Limits limits = resources.getLimits();
        Map<String, Quantity> limit = new HashMap<>(8);
        if (null != limits){
            limit.put("cpu",Quantity.fromString(limits.getCpu()));
            limit.put("memory",Quantity.fromString(limits.getMemory()));
        }

        Resources.Requests requests = resources.getRequests();
        Map<String, Quantity> request = new HashMap<>(8);
        if (null != requests){
            request.put("cpu",Quantity.fromString(requests.getCpu()));
            request.put("memory",Quantity.fromString(requests.getMemory()));
        }

        v1ResourceRequirements.setLimits(limit);
        v1ResourceRequirements.setRequests(request);

        return v1ResourceRequirements;
    }

    private static V1Probe buildV1Probe(Probe probe) {
        V1Probe v1Probe = new V1Probe();
        TCPSocket tcpSocket = probe.getTcpSocket();
        if (Objects.nonNull(tcpSocket)){
            V1TCPSocketAction tcpSocketAction = new V1TCPSocketAction();
            tcpSocketAction.setHost(tcpSocket.getHost());
            tcpSocketAction.setPort(new IntOrString(tcpSocket.getPort()));
            v1Probe.setTcpSocket(tcpSocketAction);
        }
        Exec exec = probe.getExec();
        if (Objects.nonNull(exec)){
            V1ExecAction v1ExecAction = new V1ExecAction();
            v1ExecAction.setCommand(exec.getCommand());
            v1Probe.setExec(v1ExecAction);
        }
        HttpGet httpGet = probe.getHttpGet();
        if (Objects.nonNull(httpGet)){
            V1HTTPGetAction httpGetAction = new V1HTTPGetAction();
            httpGetAction.setHost(httpGet.getHost());
            httpGetAction.setPort(new IntOrString(httpGet.getPort()));
            httpGetAction.setPath(httpGet.getPath());

            List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders().stream()
                    .map(httpHeader -> {
                        V1HTTPHeader v1HTTPHeader = new V1HTTPHeader();
                        v1HTTPHeader.setName(httpHeader.getName());
                        v1HTTPHeader.setValue(httpHeader.getValue());
                        return v1HTTPHeader;
                    }).collect(Collectors.toList());
            httpGetAction.setHttpHeaders(httpHeaders);

            v1Probe.setHttpGet(httpGetAction);
        }

        v1Probe.setPeriodSeconds(probe.getPeriodSeconds());
        v1Probe.setSuccessThreshold(probe.getSuccessThreshold());
        v1Probe.setTimeoutSeconds(probe.getTimeoutSeconds());
        v1Probe.setInitialDelaySeconds(probe.getInitialDelaySeconds());
        v1Probe.setFailureThreshold(probe.getFailureThreshold());

        return v1Probe;
    }

    private static List<V1ContainerPort> buildV1ContainerPort(List<Port> ports) {

        return ports.stream().map(port -> {

            V1ContainerPort v1ContainerPort = new V1ContainerPort();
            v1ContainerPort.setProtocol(port.getProtocol().name());
            v1ContainerPort.setContainerPort(port.getContainerPort());
            v1ContainerPort.setHostIP(port.getHostIp());
            v1ContainerPort.setHostPort(port.getHostPort());
            v1ContainerPort.setName(port.getName());

            return v1ContainerPort;
        }).collect(Collectors.toList());
    }
}
