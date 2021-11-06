package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class VolumeBuilder {
    private VolumeBuilder() {
    }

    public static V1Volume build(Volume volume) {
        V1Volume v1Volume = new V1Volume();
        if (volume.isConfigMap()) {
            V1ConfigMapVolumeSource v1ConfigMapVolumeSource = build(volume.getConfigMap());
            v1Volume.setConfigMap(v1ConfigMapVolumeSource);
        } else if (volume.isEmptyDir()) {
            V1EmptyDirVolumeSource v1EmptyDirVolumeSource = build(volume.getEmptyDir());
            v1Volume.setEmptyDir(v1EmptyDirVolumeSource);
        } else if (volume.isHostPath()) {
            V1HostPathVolumeSource v1HostPathVolumeSource = build(volume.getHostPath());
            v1Volume.setHostPath(v1HostPathVolumeSource);
        } else if (volume.isNfs()) {
            V1NFSVolumeSource v1NFSVolumeSource = build(volume.getNfs());
            v1Volume.setNfs(v1NFSVolumeSource);
        } else if (volume.isGitRepo()) {
            V1GitRepoVolumeSource v1GitRepoVolumeSource = build(volume.getGitRepo());
            v1Volume.setGitRepo(v1GitRepoVolumeSource);
        }
        return v1Volume;
    }

    public static V1ConfigMapVolumeSource build(ConfigMapVolume configMapVolume) {
        V1ConfigMapVolumeSource v1ConfigMapVolumeSource = new V1ConfigMapVolumeSource();
        v1ConfigMapVolumeSource.setName(configMapVolume.getName());
        v1ConfigMapVolumeSource.setDefaultMode(configMapVolume.getDefaultModel());
        v1ConfigMapVolumeSource.setOptional(configMapVolume.isOptional());
        List<V1KeyToPath> v1KeyToPaths = configMapVolume.getItems()
                .stream()
                .map(item -> {
                    V1KeyToPath v1KeyToPath = new V1KeyToPath();
                    v1KeyToPath.setKey(item.getKey());
                    v1KeyToPath.setMode(item.getMode());
                    v1KeyToPath.setPath(item.getPath());
                    return v1KeyToPath;
                }).collect(Collectors.toList());

        v1ConfigMapVolumeSource.setItems(v1KeyToPaths);

        return v1ConfigMapVolumeSource;
    }

    public static V1EmptyDirVolumeSource build(EmptyDirVolume emptyDirVolume) {
        V1EmptyDirVolumeSource v1EmptyDirVolumeSource = new V1EmptyDirVolumeSource();
        v1EmptyDirVolumeSource.setMedium(emptyDirVolume.getMedium());
        Quantity quantity = Quantity.fromString(emptyDirVolume.getSizeLimit());
        v1EmptyDirVolumeSource.setSizeLimit(quantity);

        return v1EmptyDirVolumeSource;
    }

    public static V1HostPathVolumeSource build(HostPathVolume hostPathVolume) {
        V1HostPathVolumeSource v1HostPathVolumeSource = new V1HostPathVolumeSource();
        v1HostPathVolumeSource.setType(hostPathVolume.getType());
        v1HostPathVolumeSource.setPath(hostPathVolume.getPath());

        return v1HostPathVolumeSource;
    }

    public static V1GitRepoVolumeSource build(GitRepoVolume gitRepoVolume) {
        V1GitRepoVolumeSource v1GitRepoVolumeSource = new V1GitRepoVolumeSource();
        v1GitRepoVolumeSource.setDirectory(gitRepoVolume.getDirectory());
        v1GitRepoVolumeSource.setRepository(gitRepoVolume.getRepository());
        v1GitRepoVolumeSource.setRevision(gitRepoVolume.getRevision());

        return v1GitRepoVolumeSource;
    }

    public static V1NFSVolumeSource build(NFSVolume nfsVolume) {
        V1NFSVolumeSource v1NFSVolumeSource = new V1NFSVolumeSource();
        v1NFSVolumeSource.setPath(nfsVolume.getPath());
        v1NFSVolumeSource.setReadOnly(nfsVolume.getReadOnly());
        v1NFSVolumeSource.setServer(nfsVolume.getServer());

        return v1NFSVolumeSource;
    }

}
