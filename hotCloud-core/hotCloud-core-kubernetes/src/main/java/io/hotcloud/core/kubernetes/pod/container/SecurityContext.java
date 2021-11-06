package io.hotcloud.core.kubernetes.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class SecurityContext {

    private Long runAsUser;
    private Boolean runAsNonRoot;
    private Boolean privileged;
    private Long runAsGroup;
}
