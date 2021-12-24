package io.hotcloud.kubernetes.api.pod.container;

import io.hotcloud.kubernetes.model.pod.container.*;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ContainerBuilder {
    private ContainerBuilder() {
    }

    public static V1Container build(Container container) {
        V1Container v1Container = new V1Container();

        List<V1ContainerPort> v1ContainerPorts = ContainerPortBuilder.build(container.getPorts());
        v1Container.setPorts(v1ContainerPorts);

        Probe readinessProbe = container.getReadinessProbe();
        if (null != readinessProbe) {
            V1Probe v1ReadinessProbe = ProbeBuilder.build(readinessProbe);
            v1Container.setReadinessProbe(v1ReadinessProbe);
        }

        Probe livenessProbe = container.getLivenessProbe();
        if (null != livenessProbe) {
            V1Probe v1LivenessProbe = ProbeBuilder.build(livenessProbe);
            v1Container.setLivenessProbe(v1LivenessProbe);
        }

        Probe containerStartupProbe = container.getStartupProbe();
        if (null != containerStartupProbe) {
            V1Probe startupProbe = ProbeBuilder.build(containerStartupProbe);
            v1Container.setStartupProbe(startupProbe);
        }

        Resources resources = container.getResources();
        if (null != resources) {
            V1ResourceRequirements v1ResourceRequirements = ResourceRequirementsBuilder.build(resources);
            v1Container.setResources(v1ResourceRequirements);
        }

        List<V1EnvVar> v1EnvVars = EnvVarsBuilder.build(container.getEnv());
        v1Container.setEnv(v1EnvVars);

        List<V1EnvFromSource> v1EnvFromSources = EnvFromSourceBuilder.build(container.getEnvFrom());
        v1Container.setEnvFrom(v1EnvFromSources);

        SecurityContext securityContext = container.getSecurityContext();
        if (Objects.nonNull(securityContext)) {
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
}
