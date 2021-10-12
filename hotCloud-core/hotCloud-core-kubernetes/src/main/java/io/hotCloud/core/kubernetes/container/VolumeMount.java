package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class VolumeMount {
    private String mountPath;
    private String name;
    private Boolean readOnly;
    private String subPath;
    private String subPathExpr;
    private String mountPropagation;

}
