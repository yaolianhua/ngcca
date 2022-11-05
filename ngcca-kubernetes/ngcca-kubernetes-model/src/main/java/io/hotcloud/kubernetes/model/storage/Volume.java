package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 * TODO Other volume types are supported
 **/
@Data
public class Volume {

    private ConfigMapVolume configMap;
    private EmptyDirVolume emptyDir;
    private GitRepoVolume gitRepo;
    private HostPathVolume hostPath;
    private NFSVolume nfs;
    private SecretVolume secretVolume;
    private PersistentVolumeClaimVolume persistentVolumeClaim;

    private String name;

    public boolean exist() {
        return Objects.nonNull(configMap) ||
                Objects.nonNull(emptyDir) ||
                Objects.nonNull(gitRepo) ||
                Objects.nonNull(hostPath) ||
                Objects.nonNull(nfs) ||
                Objects.nonNull(secretVolume) ||
                Objects.nonNull(persistentVolumeClaim);
    }

    public boolean isPersistentVolumeClaim() {
        return persistentVolumeClaim != null;
    }

    public boolean isSecret() {
        return secretVolume != null;
    }

    public boolean isConfigMap() {
        return configMap != null;
    }

    public boolean isEmptyDir() {
        return emptyDir != null;
    }

    public boolean isGitRepo() {
        return gitRepo != null;
    }

    public boolean isHostPath() {
        return hostPath != null;
    }

    public boolean isNfs() {
        return nfs != null;
    }


}
