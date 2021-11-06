package io.hotcloud.core.kubernetes.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Env {
    private String name;
    private String value;
    private EnvSource valueFrom;
}
