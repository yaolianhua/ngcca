package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class SecurityContext {

    private Long runAsUser;
    private Boolean runAsNonRoot;
    private Boolean privileged;
    private Long runAsGroup;
}
