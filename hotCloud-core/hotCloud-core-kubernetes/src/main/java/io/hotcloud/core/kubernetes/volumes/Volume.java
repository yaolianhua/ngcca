package io.hotcloud.core.kubernetes.volumes;

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

    public boolean exist() {
        return Objects.nonNull(configMap) ||
                Objects.nonNull(emptyDir) ||
                Objects.nonNull(gitRepo) ||
                Objects.nonNull(hostPath) ||
                Objects.nonNull(nfs);
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
