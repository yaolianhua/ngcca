package io.hotcloud.kubernetes.model.pod;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodSecurityContext {

    private Long runAsUser;
    private Boolean runAsNonRoot;
    private Long runAsGroup;

    public PodSecurityContext() {
    }

    public PodSecurityContext(Long runAsUser, Boolean runAsNonRoot, Long runAsGroup) {
        this.runAsUser = runAsUser;
        this.runAsNonRoot = runAsNonRoot;
        this.runAsGroup = runAsGroup;
    }
}
