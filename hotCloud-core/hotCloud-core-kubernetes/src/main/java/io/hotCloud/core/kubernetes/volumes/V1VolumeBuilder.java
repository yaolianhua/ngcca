package io.hotCloud.core.kubernetes.volumes;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1VolumeBuilder {
    private V1VolumeBuilder() {
    }

    public static V1Volume build(Volume volume) {
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
}
